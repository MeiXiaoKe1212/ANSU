package com.ansu.api.service;

import com.ansu.api.domain.entity.Order;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 订单服务接口
 */
public interface OrderService {
    
    /**
     * 根据租户ID分页查询订单
     */
    IPage<Order> getOrdersByTenantId(Long tenantId, int page, int size);
    
    /**
     * 根据租户ID获取所有订单
     */
    List<Order> getAllOrdersByTenantId(Long tenantId);
    
    /**
     * 根据ID和租户ID查询订单
     */
    Order getOrderByIdAndTenantId(Long id, Long tenantId);
    
    /**
     * 创建订单
     */
    Order createOrder(Order order);
    
    /**
     * 更新订单
     */
    Order updateOrder(Order order);
    
    /**
     * 删除订单
     */
    boolean deleteOrder(Long id, Long tenantId);
    
    /**
     * 根据状态和租户ID查询订单
     */
    List<Order> getOrdersByStatusAndTenantId(Integer status, Long tenantId);
}
