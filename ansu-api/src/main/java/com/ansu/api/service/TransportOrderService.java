package com.ansu.api.service;

import com.ansu.api.domain.entity.TransportOrder;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 运输订单服务接口
 */
public interface TransportOrderService extends IService<TransportOrder> {

    /**
     * 分页查询运输订单
     */
    IPage<TransportOrder> pageOrders(Page<TransportOrder> page, String keyword, String transportStatus, String paymentStatus);

    /**
     * 创建运输订单
     */
    TransportOrder createOrder(TransportOrder order);

    /**
     * 更新运输状态
     */
    boolean updateTransportStatus(Long orderId, String newStatus, String reason, Long operatorId, String operatorName);

    /**
     * 更新款项状态
     */
    boolean updatePaymentStatus(Long orderId, String newStatus, String reason, Long operatorId, String operatorName);

    /**
     * 计算订单利润
     */
    void calculateProfit(Long orderId);

    /**
     * 获取订单统计数据
     */
    Map<String, Object> getOrderStatistics();

    /**
     * 根据状态获取订单数量
     */
    Map<String, Long> getOrderCountByStatus();

    /**
     * 获取月度收入统计
     */
    List<Map<String, Object>> getMonthlyRevenue();
}
