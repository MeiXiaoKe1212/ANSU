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
     * 分页查询运输订单（按用户ID）
     */
    IPage<TransportOrder> pageOrdersByUserId(Page<TransportOrder> page, Long userId, String keyword, String transportStatus, String paymentStatus);

    /**
     * 根据ID和用户ID查询订单
     */
    TransportOrder getByIdAndUserId(Long id, Long userId);

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
     * 获取订单统计数据（按用户ID）
     */
    Map<String, Object> getOrderStatisticsByUserId(Long userId);

    /**
     * 根据状态获取订单数量
     */
    Map<String, Long> getOrderCountByStatus();

    /**
     * 根据状态获取订单数量（按用户ID）
     */
    Map<String, Long> getOrderCountByStatusAndUserId(Long userId);

    /**
     * 获取月度收入统计
     */
    List<Map<String, Object>> getMonthlyRevenue();

    /**
     * 获取月度收入统计（按用户ID）
     */
    List<Map<String, Object>> getMonthlyRevenueByUserId(Long userId);
}
