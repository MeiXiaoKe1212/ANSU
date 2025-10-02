package com.ansu.api.service;

import com.ansu.api.domain.entity.OrderEvent;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 订单事件服务接口
 */
public interface OrderEventService extends IService<OrderEvent> {

    /**
     * 根据订单ID查询事件列表
     */
    List<OrderEvent> getEventsByOrderId(Long orderId);

    /**
     * 添加订单事件
     */
    OrderEvent addEvent(OrderEvent event);

    /**
     * 解决事件
     */
    boolean resolveEvent(Long eventId, String resolutionDescription, Long operatorId);

    /**
     * 删除事件
     */
    boolean deleteEvent(Long eventId);
}
