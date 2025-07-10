package com.ansu.api.controller;

import com.ansu.api.domain.entity.TransportOrder;
import com.ansu.api.service.TransportOrderService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 运输订单控制器
 */
@RestController
@RequestMapping("/transport-orders")
@CrossOrigin(origins = "*")
public class TransportOrderController {

    @Autowired
    private TransportOrderService transportOrderService;

    /**
     * 分页查询运输订单
     */
    @GetMapping("/page")
    public Map<String, Object> pageOrders(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String transportStatus,
            @RequestParam(required = false) String paymentStatus) {
        
        Page<TransportOrder> page = new Page<>(current, size);
        IPage<TransportOrder> result = transportOrderService.pageOrders(page, keyword, transportStatus, paymentStatus);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "查询成功");
        response.put("data", result);
        
        return response;
    }

    /**
     * 根据ID查询订单详情
     */
    @GetMapping("/{id}")
    public Map<String, Object> getOrderById(@PathVariable Long id) {
        TransportOrder order = transportOrderService.getById(id);
        
        Map<String, Object> response = new HashMap<>();
        if (order != null) {
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", order);
        } else {
            response.put("code", 404);
            response.put("message", "订单不存在");
        }
        
        return response;
    }

    /**
     * 创建运输订单
     */
    @PostMapping
    public Map<String, Object> createOrder(@RequestBody TransportOrder order) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 设置创建人ID（实际项目中应该从登录用户获取）
            order.setCreateUserId(1L);
            
            TransportOrder savedOrder = transportOrderService.createOrder(order);
            
            response.put("code", 200);
            response.put("message", "创建成功");
            response.put("data", savedOrder);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "创建失败：" + e.getMessage());
        }
        
        return response;
    }

    /**
     * 更新运输订单
     */
    @PutMapping("/{id}")
    public Map<String, Object> updateOrder(@PathVariable Long id, @RequestBody TransportOrder order) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            order.setId(id);
            boolean success = transportOrderService.updateById(order);
            
            if (success) {
                // 重新计算利润
                transportOrderService.calculateProfit(id);
                
                response.put("code", 200);
                response.put("message", "更新成功");
            } else {
                response.put("code", 500);
                response.put("message", "更新失败");
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "更新失败：" + e.getMessage());
        }
        
        return response;
    }

    /**
     * 删除运输订单
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteOrder(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean success = transportOrderService.removeById(id);
            
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

    /**
     * 更新运输状态
     */
    @PutMapping("/{id}/transport-status")
    public Map<String, Object> updateTransportStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String reason) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 实际项目中应该从登录用户获取操作人信息
            boolean success = transportOrderService.updateTransportStatus(id, status, reason, 1L, "管理员");
            
            if (success) {
                response.put("code", 200);
                response.put("message", "状态更新成功");
            } else {
                response.put("code", 500);
                response.put("message", "状态更新失败");
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "状态更新失败：" + e.getMessage());
        }
        
        return response;
    }

    /**
     * 更新款项状态
     */
    @PutMapping("/{id}/payment-status")
    public Map<String, Object> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String reason) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 实际项目中应该从登录用户获取操作人信息
            boolean success = transportOrderService.updatePaymentStatus(id, status, reason, 1L, "管理员");
            
            if (success) {
                response.put("code", 200);
                response.put("message", "状态更新成功");
            } else {
                response.put("code", 500);
                response.put("message", "状态更新失败");
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "状态更新失败：" + e.getMessage());
        }
        
        return response;
    }

    /**
     * 获取订单统计数据
     */
    @GetMapping("/statistics")
    public Map<String, Object> getStatistics() {
        Map<String, Object> statistics = transportOrderService.getOrderStatistics();
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "查询成功");
        response.put("data", statistics);
        
        return response;
    }

    /**
     * 获取月度收入统计
     */
    @GetMapping("/monthly-revenue")
    public Map<String, Object> getMonthlyRevenue() {
        List<Map<String, Object>> monthlyData = transportOrderService.getMonthlyRevenue();
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "查询成功");
        response.put("data", monthlyData);
        
        return response;
    }
}
