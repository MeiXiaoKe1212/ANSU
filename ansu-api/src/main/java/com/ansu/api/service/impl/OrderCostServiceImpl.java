package com.ansu.api.service.impl;

import com.ansu.api.domain.entity.OrderCost;
import com.ansu.api.mapper.OrderCostMapper;
import com.ansu.api.service.OrderCostService;
import com.ansu.api.service.TransportOrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 订单成本服务实现类
 */
@Service
public class OrderCostServiceImpl extends ServiceImpl<OrderCostMapper, OrderCost> implements OrderCostService {

    @Autowired
    private TransportOrderService transportOrderService;

    @Override
    public List<OrderCost> getCostsByOrderId(Long orderId) {
        LambdaQueryWrapper<OrderCost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderCost::getOrderId, orderId);
        wrapper.orderByDesc(OrderCost::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    @Transactional
    public OrderCost addCost(OrderCost cost) {
        // 设置创建人ID（实际项目中应该从登录用户获取）
        if (cost.getCreateUserId() == null) {
            cost.setCreateUserId(1L);
        }
        
        this.save(cost);
        
        // 重新计算订单利润
        transportOrderService.calculateProfit(cost.getOrderId());
        
        return cost;
    }

    @Override
    @Transactional
    public boolean deleteCost(Long costId, Long orderId) {
        boolean success = this.removeById(costId);
        
        if (success) {
            // 重新计算订单利润
            transportOrderService.calculateProfit(orderId);
        }
        
        return success;
    }
}
