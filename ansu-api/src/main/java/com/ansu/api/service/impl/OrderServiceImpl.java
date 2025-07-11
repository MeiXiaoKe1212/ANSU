package com.ansu.api.service.impl;

import com.ansu.api.domain.entity.Order;
import com.ansu.api.mapper.OrderMapper;
import com.ansu.api.service.OrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单服务实现类
 */
@Service
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Override
    public IPage<Order> getOrdersByTenantId(Long tenantId, int page, int size) {
        Page<Order> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getTenantId, tenantId)
               .orderByDesc(Order::getCreateTime);
        return orderMapper.selectPage(pageParam, wrapper);
    }
    
    @Override
    public List<Order> getAllOrdersByTenantId(Long tenantId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getTenantId, tenantId)
               .orderByDesc(Order::getCreateTime);
        return orderMapper.selectList(wrapper);
    }
    
    @Override
    public Order getOrderByIdAndTenantId(Long id, Long tenantId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getId, id)
               .eq(Order::getTenantId, tenantId);
        return orderMapper.selectOne(wrapper);
    }
    
    @Override
    public Order createOrder(Order order) {
        orderMapper.insert(order);
        return order;
    }
    
    @Override
    public Order updateOrder(Order order) {
        // 确保只能更新同租户的订单
        Order existingOrder = getOrderByIdAndTenantId(order.getId(), order.getTenantId());
        if (existingOrder == null) {
            throw new RuntimeException("订单不存在或无权限访问");
        }
        
        orderMapper.updateById(order);
        return order;
    }
    
    @Override
    public boolean deleteOrder(Long id, Long tenantId) {
        // 确保只能删除同租户的订单
        Order existingOrder = getOrderByIdAndTenantId(id, tenantId);
        if (existingOrder == null) {
            throw new RuntimeException("订单不存在或无权限访问");
        }
        
        return orderMapper.deleteById(id) > 0;
    }
    
    @Override
    public List<Order> getOrdersByStatusAndTenantId(Integer status, Long tenantId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getStatus, status)
               .eq(Order::getTenantId, tenantId)
               .orderByDesc(Order::getCreateTime);
        return orderMapper.selectList(wrapper);
    }
}
