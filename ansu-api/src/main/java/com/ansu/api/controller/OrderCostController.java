package com.ansu.api.controller;

import com.ansu.api.domain.dto.ApiResponse;
import com.ansu.api.domain.entity.OrderCost;
import com.ansu.api.domain.entity.SysUser;
import com.ansu.api.domain.entity.TransportOrder;
import com.ansu.api.service.OrderCostService;
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
 * 订单成本控制器
 */
@RestController
@RequestMapping("/order-costs")
public class OrderCostController {

    @Autowired
    private OrderCostService orderCostService;

    @Autowired
    private TransportOrderService transportOrderService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 根据订单ID查询成本列表
     */
    @GetMapping("/order/{orderId}")
    public ApiResponse<List<OrderCost>> getCostsByOrderId(@PathVariable Long orderId, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);

            // 验证订单是否属于当前用户
            TransportOrder order = transportOrderService.getByIdAndUserId(orderId, userId);
            if (order == null) {
                return ApiResponse.error(404, "订单不存在或无权访问");
            }

            List<OrderCost> costs = orderCostService.getCostsByOrderId(orderId);
            return ApiResponse.success(costs);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 添加订单成本
     */
    @PostMapping
    public ApiResponse<OrderCost> addCost(@RequestBody OrderCost cost, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);

            // 验证订单是否属于当前用户
            TransportOrder order = transportOrderService.getByIdAndUserId(cost.getOrderId(), userId);
            if (order == null) {
                return ApiResponse.error(404, "订单不存在或无权访问");
            }

            OrderCost savedCost = orderCostService.addCost(cost);
            return ApiResponse.success("添加成功", savedCost);
        } catch (Exception e) {
            return ApiResponse.error("添加失败：" + e.getMessage());
        }
    }

    /**
     * 更新订单成本
     */
    @PutMapping("/{id}")
    public ApiResponse<Void> updateCost(@PathVariable Long id, @RequestBody OrderCost cost, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);

            // 先获取现有成本记录
            OrderCost existingCost = orderCostService.getById(id);
            if (existingCost == null) {
                return ApiResponse.error(404, "成本记录不存在");
            }

            // 验证订单是否属于当前用户
            TransportOrder order = transportOrderService.getByIdAndUserId(existingCost.getOrderId(), userId);
            if (order == null) {
                return ApiResponse.error(404, "订单不存在或无权访问");
            }

            cost.setId(id);
            boolean success = orderCostService.updateById(cost);

            if (success) {
                return ApiResponse.success("更新成功", null);
            } else {
                return ApiResponse.error("更新失败");
            }
        } catch (Exception e) {
            return ApiResponse.error("更新失败：" + e.getMessage());
        }
    }

    /**
     * 删除订单成本
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCost(@PathVariable Long id, @RequestParam Long orderId, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);

            // 先获取现有成本记录
            OrderCost existingCost = orderCostService.getById(id);
            if (existingCost == null) {
                return ApiResponse.error(404, "成本记录不存在");
            }

            // 验证订单是否属于当前用户
            TransportOrder order = transportOrderService.getByIdAndUserId(existingCost.getOrderId(), userId);
            if (order == null) {
                return ApiResponse.error(404, "订单不存在或无权访问");
            }

            boolean success = orderCostService.deleteCost(id, orderId);

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
