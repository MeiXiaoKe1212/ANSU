package com.ansu.api.controller;

import com.ansu.api.domain.dto.ApiResponse;
import com.ansu.api.domain.entity.SysUser;
import com.ansu.api.domain.entity.TransportOrder;
import com.ansu.api.service.TransportOrderService;
import com.ansu.api.service.UserService;
import com.ansu.api.utils.JwtUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 运输订单控制器
 */
@RestController
@RequestMapping("/transport-orders")
public class TransportOrderController {

    @Autowired
    private TransportOrderService transportOrderService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 分页查询运输订单
     */
    @GetMapping("/page")
    public ApiResponse<IPage<TransportOrder>> pageOrders(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String transportStatus,
            @RequestParam(required = false) String paymentStatus,
            HttpServletRequest request) {

        try {
            Long userId = getUserIdFromRequest(request);
            Page<TransportOrder> page = new Page<>(current, size);
            IPage<TransportOrder> result = transportOrderService.pageOrdersByUserId(page, userId, keyword, transportStatus, paymentStatus);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 根据ID查询订单详情
     */
    @GetMapping("/{id}")
    public ApiResponse<TransportOrder> getOrderById(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            TransportOrder order = transportOrderService.getByIdAndUserId(id, userId);

            if (order != null) {
                return ApiResponse.success(order);
            } else {
                return ApiResponse.error(404, "订单不存在或无权访问");
            }
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 创建运输订单
     */
    @PostMapping
    public ApiResponse<TransportOrder> createOrder(@RequestBody TransportOrder order, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            order.setCreateUserId(userId);

            TransportOrder savedOrder = transportOrderService.createOrder(order);
            return ApiResponse.success("创建成功", savedOrder);
        } catch (Exception e) {
            return ApiResponse.error("创建失败：" + e.getMessage());
        }
    }

    /**
     * 更新运输订单
     */
    @PutMapping("/{id}")
    public ApiResponse<TransportOrder> updateOrder(@PathVariable Long id, @RequestBody TransportOrder order, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);

            // 先检查订单是否存在且属于当前用户
            TransportOrder existingOrder = transportOrderService.getByIdAndUserId(id, userId);
            if (existingOrder == null) {
                return ApiResponse.error(404, "订单不存在或无权访问");
            }

            // 设置订单ID和创建人ID
            order.setId(id);
            order.setCreateUserId(userId);

            boolean success = transportOrderService.updateById(order);

            if (success) {
                // 重新计算利润
                transportOrderService.calculateProfit(id);

                // 获取更新后的订单
                TransportOrder updatedOrder = transportOrderService.getByIdAndUserId(id, userId);
                return ApiResponse.success("更新成功", updatedOrder);
            } else {
                return ApiResponse.error("更新失败");
            }
        } catch (Exception e) {
            return ApiResponse.error("更新失败：" + e.getMessage());
        }
    }

    /**
     * 删除运输订单
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteOrder(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);

            // 先检查订单是否存在且属于当前用户
            TransportOrder existingOrder = transportOrderService.getByIdAndUserId(id, userId);
            if (existingOrder == null) {
                return ApiResponse.error(404, "订单不存在或无权访问");
            }

            boolean success = transportOrderService.removeById(id);

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
     * 更新运输状态
     */
    @PutMapping("/{id}/transport-status")
    public ApiResponse<Void> updateTransportStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String reason,
            HttpServletRequest request) {

        try {
            Long userId = getUserIdFromRequest(request);

            // 先检查订单是否存在且属于当前用户
            TransportOrder existingOrder = transportOrderService.getByIdAndUserId(id, userId);
            if (existingOrder == null) {
                return ApiResponse.error(404, "订单不存在或无权访问");
            }

            // 获取用户信息
            SysUser user = userService.findById(userId);
            String operatorName = user != null ? user.getRealName() : "未知用户";

            boolean success = transportOrderService.updateTransportStatus(id, status, reason, userId, operatorName);

            if (success) {
                return ApiResponse.success("状态更新成功", null);
            } else {
                return ApiResponse.error("状态更新失败");
            }
        } catch (Exception e) {
            return ApiResponse.error("状态更新失败：" + e.getMessage());
        }
    }

    /**
     * 更新款项状态
     */
    @PutMapping("/{id}/payment-status")
    public ApiResponse<Void> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String reason,
            HttpServletRequest request) {

        try {
            Long userId = getUserIdFromRequest(request);

            // 先检查订单是否存在且属于当前用户
            TransportOrder existingOrder = transportOrderService.getByIdAndUserId(id, userId);
            if (existingOrder == null) {
                return ApiResponse.error(404, "订单不存在或无权访问");
            }

            // 获取用户信息
            SysUser user = userService.findById(userId);
            String operatorName = user != null ? user.getRealName() : "未知用户";

            boolean success = transportOrderService.updatePaymentStatus(id, status, reason, userId, operatorName);

            if (success) {
                return ApiResponse.success("状态更新成功", null);
            } else {
                return ApiResponse.error("状态更新失败");
            }
        } catch (Exception e) {
            return ApiResponse.error("状态更新失败：" + e.getMessage());
        }
    }

    /**
     * 获取订单统计数据
     */
    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getStatistics(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            Map<String, Object> statistics = transportOrderService.getOrderStatisticsByUserId(userId);
            return ApiResponse.success("查询成功", statistics);
        } catch (Exception e) {
            return ApiResponse.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取月度收入统计
     */
    @GetMapping("/monthly-revenue")
    public ApiResponse<List<Map<String, Object>>> getMonthlyRevenue(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            List<Map<String, Object>> monthlyData = transportOrderService.getMonthlyRevenueByUserId(userId);
            return ApiResponse.success(monthlyData);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
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
