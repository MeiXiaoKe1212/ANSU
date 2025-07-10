package com.ansu.api.controller;

import com.ansu.api.domain.entity.OrderEvent;
import com.ansu.api.service.OrderEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单事件控制器
 */
@RestController
@RequestMapping("/order-events")
@CrossOrigin(origins = "*")
public class OrderEventController {

    @Autowired
    private OrderEventService orderEventService;

    /**
     * 根据订单ID查询事件列表
     */
    @GetMapping("/order/{orderId}")
    public Map<String, Object> getEventsByOrderId(@PathVariable Long orderId) {
        List<OrderEvent> events = orderEventService.getEventsByOrderId(orderId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "查询成功");
        response.put("data", events);
        
        return response;
    }

    /**
     * 添加订单事件
     */
    @PostMapping
    public Map<String, Object> addEvent(@RequestBody OrderEvent event) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            OrderEvent savedEvent = orderEventService.addEvent(event);
            
            response.put("code", 200);
            response.put("message", "添加成功");
            response.put("data", savedEvent);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "添加失败：" + e.getMessage());
        }
        
        return response;
    }

    /**
     * 解决事件
     */
    @PutMapping("/{id}/resolve")
    public Map<String, Object> resolveEvent(@PathVariable Long id, @RequestParam String resolutionDescription) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean success = orderEventService.resolveEvent(id, resolutionDescription, 1L);
            
            if (success) {
                response.put("code", 200);
                response.put("message", "事件已解决");
            } else {
                response.put("code", 500);
                response.put("message", "解决失败");
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "解决失败：" + e.getMessage());
        }
        
        return response;
    }

    /**
     * 删除事件
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteEvent(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean success = orderEventService.deleteEvent(id);
            
            if (success) {
                response.put("code", 200);
                response.put("message", "删除成功");
            } else {
                response.put("code", 500);
                response.put("message", "删除失败");
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "删除失败：" + e.getMessage());
        }
        
        return response;
    }
}
