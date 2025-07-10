package com.ansu.api.service;

import com.ansu.api.domain.entity.OrderCost;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 订单成本服务接口
 */
public interface OrderCostService extends IService<OrderCost> {

    /**
     * 根据订单ID查询成本列表
     */
    List<OrderCost> getCostsByOrderId(Long orderId);

    /**
     * 添加订单成本
     */
    OrderCost addCost(OrderCost cost);

    /**
     * 删除订单成本
     */
    boolean deleteCost(Long costId, Long orderId);
}
