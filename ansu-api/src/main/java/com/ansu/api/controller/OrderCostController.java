package com.ansu.api.controller;

import com.ansu.api.domain.entity.OrderCost;
import com.ansu.api.service.OrderCostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单成本控制器
 */
@RestController
@RequestMapping("/order-costs")
@CrossOrigin(origins = "*")
public class OrderCostController {

    @Autowired
    private OrderCostService orderCostService;

    /**
     * 根据订单ID查询成本列表
     */
    @GetMapping("/order/{orderId}")
    public Map<String, Object> getCostsByOrderId(@PathVariable Long orderId) {
        List<OrderCost> costs = orderCostService.getCostsByOrderId(orderId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "查询成功");
        response.put("data", costs);
        
        return response;
    }

    /**
     * 添加订单成本
     */
    @PostMapping
    public Map<String, Object> addCost(@RequestBody OrderCost cost) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            OrderCost savedCost = orderCostService.addCost(cost);
            
            response.put("code", 200);
            response.put("message", "添加成功");
            response.put("data", savedCost);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "添加失败：" + e.getMessage());
        }
        
        return response;
    }

    /**
     * 更新订单成本
     */
    @PutMapping("/{id}")
    public Map<String, Object> updateCost(@PathVariable Long id, @RequestBody OrderCost cost) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            cost.setId(id);
            boolean success = orderCostService.updateById(cost);
            
            if (success) {
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
     * 删除订单成本
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteCost(@PathVariable Long id, @RequestParam Long orderId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean success = orderCostService.deleteCost(id, orderId);
            
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
