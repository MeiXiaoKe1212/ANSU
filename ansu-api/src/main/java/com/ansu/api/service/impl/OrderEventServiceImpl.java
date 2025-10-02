package com.ansu.api.service.impl;

import com.ansu.api.domain.entity.OrderEvent;
import com.ansu.api.mapper.OrderEventMapper;
import com.ansu.api.service.OrderEventService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单事件服务实现类
 */
@Service
public class OrderEventServiceImpl extends ServiceImpl<OrderEventMapper, OrderEvent> implements OrderEventService {

    @Override
    public List<OrderEvent> getEventsByOrderId(Long orderId) {
        LambdaQueryWrapper<OrderEvent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderEvent::getOrderId, orderId);
        wrapper.orderByDesc(OrderEvent::getEventTime);
        return this.list(wrapper);
    }

    @Override
    public OrderEvent addEvent(OrderEvent event) {
        // 设置创建人ID（实际项目中应该从登录用户获取）
        if (event.getCreateUserId() == null) {
            event.setCreateUserId(1L);
        }
        
        // 设置默认值
        if (event.getImpactLevel() == null) {
            event.setImpactLevel("LOW");
        }
        if (event.getIsResolved() == null) {
            event.setIsResolved(0);
        }
        
        this.save(event);
        return event;
    }

    @Override
    public boolean resolveEvent(Long eventId, String resolutionDescription, Long operatorId) {
        OrderEvent event = this.getById(eventId);
        if (event == null) {
            return false;
        }
        
        event.setIsResolved(1);
        event.setResolutionTime(LocalDateTime.now());
        event.setResolutionDescription(resolutionDescription);
        
        return this.updateById(event);
    }

    @Override
    public boolean deleteEvent(Long eventId) {
        return this.removeById(eventId);
    }
}
