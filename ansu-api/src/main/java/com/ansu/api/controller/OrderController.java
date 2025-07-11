package com.ansu.api.controller;

import com.ansu.api.domain.dto.ApiResponse;
import com.ansu.api.domain.entity.Order;
import com.ansu.api.service.OrderService;
import com.ansu.api.util.JwtUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 分页查询订单
     */
    @GetMapping
    public ApiResponse<IPage<Order>> getOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        try {
            Long tenantId = getTenantIdFromRequest(request);
            IPage<Order> orders = orderService.getOrdersByTenantId(tenantId, page, size);
            return ApiResponse.success(orders);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取所有订单
     */
    @GetMapping("/all")
    public ApiResponse<List<Order>> getAllOrders(HttpServletRequest request) {
        try {
            Long tenantId = getTenantIdFromRequest(request);
            List<Order> orders = orderService.getAllOrdersByTenantId(tenantId);
            return ApiResponse.success(orders);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 根据ID查询订单
     */
    @GetMapping("/{id}")
    public ApiResponse<Order> getOrderById(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long tenantId = getTenantIdFromRequest(request);
            Order order = orderService.getOrderByIdAndTenantId(id, tenantId);
            if (order == null) {
                return ApiResponse.error("订单不存在");
            }
            return ApiResponse.success(order);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 创建订单
     */
    @PostMapping
    public ApiResponse<Order> createOrder(@RequestBody Order order, HttpServletRequest request) {
        try {
            Long tenantId = getTenantIdFromRequest(request);
            Long userId = getUserIdFromRequest(request);
            
            // 设置租户ID和创建人
            order.setTenantId(tenantId);
            order.setCreatedBy(userId);
            
            Order createdOrder = orderService.createOrder(order);
            return ApiResponse.success("订单创建成功", createdOrder);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 更新订单
     */
    @PutMapping("/{id}")
    public ApiResponse<Order> updateOrder(@PathVariable Long id, @RequestBody Order order, HttpServletRequest request) {
        try {
            Long tenantId = getTenantIdFromRequest(request);
            
            // 设置ID和租户ID
            order.setId(id);
            order.setTenantId(tenantId);
            
            Order updatedOrder = orderService.updateOrder(order);
            return ApiResponse.success("订单更新成功", updatedOrder);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 删除订单
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteOrder(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long tenantId = getTenantIdFromRequest(request);
            boolean deleted = orderService.deleteOrder(id, tenantId);
            if (deleted) {
                return ApiResponse.success("订单删除成功");
            } else {
                return ApiResponse.error("订单删除失败");
            }
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 根据状态查询订单
     */
    @GetMapping("/status/{status}")
    public ApiResponse<List<Order>> getOrdersByStatus(@PathVariable Integer status, HttpServletRequest request) {
        try {
            Long tenantId = getTenantIdFromRequest(request);
            List<Order> orders = orderService.getOrdersByStatusAndTenantId(status, tenantId);
            return ApiResponse.success(orders);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 从请求中获取租户ID
     */
    private Long getTenantIdFromRequest(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            throw new RuntimeException("认证令牌无效");
        }
        return jwtUtil.getTenantIdFromToken(token);
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
