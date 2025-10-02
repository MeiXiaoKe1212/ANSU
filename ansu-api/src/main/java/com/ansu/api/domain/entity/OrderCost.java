package com.ansu.api.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 订单成本实体类
 */
@TableName("order_cost")
public class OrderCost extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 成本类型：FUEL-加油费，TOLL-过路费，FINE-违章费，MAINTENANCE-维修费，OUTSOURCE-外包费，OTHER-其他
     */
    private String costType;

    /**
     * 成本名称
     */
    private String costName;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 发生日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate costDate;

    /**
     * 费用描述
     */
    private String description;

    /**
     * 凭证图片URL
     */
    private String receiptUrl;

    /**
     * 创建人ID
     */
    private Long createUserId;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getCostType() {
        return costType;
    }

    public void setCostType(String costType) {
        this.costType = costType;
    }

    public String getCostName() {
        return costName;
    }

    public void setCostName(String costName) {
        this.costName = costName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getCostDate() {
        return costDate;
    }

    public void setCostDate(LocalDate costDate) {
        this.costDate = costDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReceiptUrl() {
        return receiptUrl;
    }

    public void setReceiptUrl(String receiptUrl) {
        this.receiptUrl = receiptUrl;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }
}