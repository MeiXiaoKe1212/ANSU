package com.ansu.api.service.impl;

import cn.hutool.core.util.StrUtil;
import com.ansu.api.domain.entity.OrderCost;
import com.ansu.api.domain.entity.OrderStatusLog;
import com.ansu.api.domain.entity.TransportOrder;
import com.ansu.api.mapper.OrderCostMapper;
import com.ansu.api.mapper.OrderStatusLogMapper;
import com.ansu.api.mapper.TransportOrderMapper;
import com.ansu.api.service.TransportOrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 运输订单服务实现类
 */
@Service
public class TransportOrderServiceImpl extends ServiceImpl<TransportOrderMapper, TransportOrder> implements TransportOrderService {

    @Autowired
    private OrderCostMapper orderCostMapper;

    @Autowired
    private OrderStatusLogMapper orderStatusLogMapper;

    @Override
    public IPage<TransportOrder> pageOrders(Page<TransportOrder> page, String keyword, String transportStatus, String paymentStatus) {
        LambdaQueryWrapper<TransportOrder> wrapper = new LambdaQueryWrapper<>();
        
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(TransportOrder::getOrderNo, keyword)
                    .or().like(TransportOrder::getCustomerCompanyName, keyword)
                    .or().like(TransportOrder::getCargoName, keyword)
                    .or().like(TransportOrder::getLicensePlate, keyword));
        }
        
        if (StrUtil.isNotBlank(transportStatus)) {
            wrapper.eq(TransportOrder::getTransportStatus, transportStatus);
        }
        
        if (StrUtil.isNotBlank(paymentStatus)) {
            wrapper.eq(TransportOrder::getPaymentStatus, paymentStatus);
        }
        
        wrapper.orderByDesc(TransportOrder::getCreateTime);
        
        return this.page(page, wrapper);
    }

    @Override
    @Transactional
    public TransportOrder createOrder(TransportOrder order) {
        // 生成订单编号
        String orderNo = generateOrderNo();
        order.setOrderNo(orderNo);
        
        // 设置默认状态
        if (StrUtil.isBlank(order.getTransportStatus())) {
            order.setTransportStatus("CREATED");
        }
        if (StrUtil.isBlank(order.getPaymentStatus())) {
            order.setPaymentStatus("UNPAID");
        }
        
        // 初始化金额字段
        if (order.getTotalCost() == null) {
            order.setTotalCost(BigDecimal.ZERO);
        }
        if (order.getProfit() == null) {
            order.setProfit(BigDecimal.ZERO);
        }
        if (order.getProfitRate() == null) {
            order.setProfitRate(BigDecimal.ZERO);
        }
        
        this.save(order);
        
        // 记录状态日志
        saveStatusLog(order.getId(), "TRANSPORT", null, order.getTransportStatus(), "创建订单", order.getCreateUserId(), "系统");
        saveStatusLog(order.getId(), "PAYMENT", null, order.getPaymentStatus(), "创建订单", order.getCreateUserId(), "系统");
        
        return order;
    }

    @Override
    @Transactional
    public boolean updateTransportStatus(Long orderId, String newStatus, String reason, Long operatorId, String operatorName) {
        TransportOrder order = this.getById(orderId);
        if (order == null) {
            return false;
        }
        
        String oldStatus = order.getTransportStatus();
        order.setTransportStatus(newStatus);
        
        // 根据状态设置时间
        if ("DEPARTED".equals(newStatus) && order.getDepartureTime() == null) {
            order.setDepartureTime(LocalDateTime.now());
        } else if ("DELIVERED".equals(newStatus) && order.getArrivalTime() == null) {
            order.setArrivalTime(LocalDateTime.now());
        }
        
        boolean result = this.updateById(order);
        
        if (result) {
            // 记录状态变更日志
            saveStatusLog(orderId, "TRANSPORT", oldStatus, newStatus, reason, operatorId, operatorName);
        }
        
        return result;
    }

    @Override
    @Transactional
    public boolean updatePaymentStatus(Long orderId, String newStatus, String reason, Long operatorId, String operatorName) {
        TransportOrder order = this.getById(orderId);
        if (order == null) {
            return false;
        }
        
        String oldStatus = order.getPaymentStatus();
        order.setPaymentStatus(newStatus);
        
        boolean result = this.updateById(order);
        
        if (result) {
            // 记录状态变更日志
            saveStatusLog(orderId, "PAYMENT", oldStatus, newStatus, reason, operatorId, operatorName);
        }
        
        return result;
    }

    @Override
    @Transactional
    public void calculateProfit(Long orderId) {
        TransportOrder order = this.getById(orderId);
        if (order == null) {
            return;
        }
        
        // 计算总成本
        LambdaQueryWrapper<OrderCost> costWrapper = new LambdaQueryWrapper<>();
        costWrapper.eq(OrderCost::getOrderId, orderId);
        List<OrderCost> costs = orderCostMapper.selectList(costWrapper);
        
        BigDecimal totalCost = costs.stream()
                .map(OrderCost::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        order.setTotalCost(totalCost);
        
        // 计算利润
        BigDecimal actualPrice = order.getActualPrice();
        if (actualPrice != null) {
            BigDecimal profit = actualPrice.subtract(totalCost);
            order.setProfit(profit);
            
            // 计算利润率
            if (actualPrice.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal profitRate = profit.divide(actualPrice, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"));
                order.setProfitRate(profitRate);
            }
        }
        
        this.updateById(order);
    }

    @Override
    public Map<String, Object> getOrderStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 总订单数
        long totalOrders = this.count();
        statistics.put("totalOrders", totalOrders);
        
        // 各状态订单数
        Map<String, Long> statusCount = getOrderCountByStatus();
        statistics.put("statusCount", statusCount);
        
        // 总收入
        LambdaQueryWrapper<TransportOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNotNull(TransportOrder::getActualPrice);
        List<TransportOrder> orders = this.list(wrapper);
        
        BigDecimal totalRevenue = orders.stream()
                .map(TransportOrder::getActualPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        statistics.put("totalRevenue", totalRevenue);
        
        // 总利润
        BigDecimal totalProfit = orders.stream()
                .map(order -> order.getProfit() != null ? order.getProfit() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        statistics.put("totalProfit", totalProfit);
        
        return statistics;
    }

    @Override
    public Map<String, Long> getOrderCountByStatus() {
        Map<String, Long> statusCount = new HashMap<>();
        
        // 运输状态统计
        String[] transportStatuses = {"CREATED", "DEPARTED", "TRANSPORTING", "EXCEPTION", "DELIVERED"};
        for (String status : transportStatuses) {
            LambdaQueryWrapper<TransportOrder> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TransportOrder::getTransportStatus, status);
            long count = this.count(wrapper);
            statusCount.put(status, count);
        }
        
        return statusCount;
    }

    @Override
    public List<Map<String, Object>> getMonthlyRevenue() {
        // 这里简化实现，实际项目中可能需要使用原生SQL查询
        List<Map<String, Object>> monthlyData = new ArrayList<>();
        
        LambdaQueryWrapper<TransportOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNotNull(TransportOrder::getActualPrice);
        wrapper.orderByDesc(TransportOrder::getCreateTime);
        List<TransportOrder> orders = this.list(wrapper);
        
        // 按月份分组统计（简化实现）
        Map<String, BigDecimal> monthlyRevenue = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        
        for (TransportOrder order : orders) {
            String month = order.getCreateTime().format(formatter);
            monthlyRevenue.merge(month, order.getActualPrice(), BigDecimal::add);
        }
        
        for (Map.Entry<String, BigDecimal> entry : monthlyRevenue.entrySet()) {
            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", entry.getKey());
            monthData.put("revenue", entry.getValue());
            monthlyData.add(monthData);
        }
        
        return monthlyData;
    }

    /**
     * 生成订单编号
     */
    private String generateOrderNo() {
        String prefix = "TO";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.valueOf((int) (Math.random() * 1000));
        return prefix + timestamp + String.format("%03d", Integer.parseInt(random));
    }

    /**
     * 保存状态变更日志
     */
    private void saveStatusLog(Long orderId, String statusType, String oldStatus, String newStatus, 
                              String reason, Long operatorId, String operatorName) {
        OrderStatusLog log = new OrderStatusLog();
        log.setOrderId(orderId);
        log.setStatusType(statusType);
        log.setOldStatus(oldStatus);
        log.setNewStatus(newStatus);
        log.setChangeReason(reason);
        log.setOperatorId(operatorId);
        log.setOperatorName(operatorName);
        orderStatusLogMapper.insert(log);
    }
}
