package com.ansu.api.controller;

import com.ansu.api.domain.dto.ApiResponse;
import com.ansu.api.domain.entity.OrderEvent;
import com.ansu.api.domain.entity.TransportOrder;
import com.ansu.api.service.OrderEventService;
import com.ansu.api.service.TransportOrderService;
import com.ansu.api.service.UserService;
import com.ansu.api.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单事件控制器
 */
@RestController
@RequestMapping("/order-events")
public class OrderEventController {

    @Autowired
    private OrderEventService orderEventService;

    @Autowired
    private TransportOrderService transportOrderService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 根据订单ID查询事件列表
     */
    @GetMapping("/order/{orderId}")
    public ApiResponse<List<OrderEvent>> getEventsByOrderId(@PathVariable Long orderId, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);

            // 验证订单是否属于当前用户
            TransportOrder order = transportOrderService.getByIdAndUserId(orderId, userId);
            if (order == null) {
                return ApiResponse.error(404, "订单不存在或无权访问");
            }

            List<OrderEvent> events = orderEventService.getEventsByOrderId(orderId);
            return ApiResponse.success("查询成功", events);
        } catch (Exception e) {
            return ApiResponse.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 添加订单事件
     */
    @PostMapping
    public ApiResponse<OrderEvent> addEvent(@RequestBody OrderEvent event, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);

            // 验证订单是否属于当前用户
            TransportOrder order = transportOrderService.getByIdAndUserId(event.getOrderId(), userId);
            if (order == null) {
                return ApiResponse.error(404, "订单不存在或无权访问");
            }

            // 设置创建人ID
            event.setCreateUserId(userId);

            OrderEvent savedEvent = orderEventService.addEvent(event);
            return ApiResponse.success("添加成功", savedEvent);
        } catch (Exception e) {
            return ApiResponse.error("添加失败：" + e.getMessage());
        }
    }

    /**
     * 解决事件
     */
    @PutMapping("/{id}/resolve")
    public ApiResponse<Void> resolveEvent(@PathVariable Long id, @RequestParam String resolutionDescription, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);

            // 先获取事件信息
            OrderEvent event = orderEventService.getById(id);
            if (event == null) {
                return ApiResponse.error(404, "事件不存在");
            }

            // 验证订单是否属于当前用户
            TransportOrder order = transportOrderService.getByIdAndUserId(event.getOrderId(), userId);
            if (order == null) {
                return ApiResponse.error(404, "订单不存在或无权访问");
            }

            boolean success = orderEventService.resolveEvent(id, resolutionDescription, userId);

            if (success) {
                return ApiResponse.success("事件已解决", null);
            } else {
                return ApiResponse.error("解决失败");
            }
        } catch (Exception e) {
            return ApiResponse.error("解决失败：" + e.getMessage());
        }
    }

    /**
     * 删除事件
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteEvent(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);

            // 先获取事件信息
            OrderEvent event = orderEventService.getById(id);
            if (event == null) {
                return ApiResponse.error(404, "事件不存在");
            }

            // 验证订单是否属于当前用户
            TransportOrder order = transportOrderService.getByIdAndUserId(event.getOrderId(), userId);
            if (order == null) {
                return ApiResponse.error(404, "订单不存在或无权访问");
            }

            boolean success = orderEventService.deleteEvent(id);

            if (success) {
                return ApiResponse.success("删除成功", null);
            } else {
                return ApiResponse.error("删除失败");
            }
        } catch (Exception e) {
            return ApiResponse.error("删除失败：" + e.getMessage());
        }
    }

    /**
     * 从请求中获取用户ID
     */
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            throw new RuntimeException("认证令牌无效");
        }
        return jwtUtil.getUserIdFromToken(token);
    }

    /**
     * 从请求中获取token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
