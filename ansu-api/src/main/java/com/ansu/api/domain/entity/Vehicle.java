package com.ansu.api.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;

/**
 * 车辆实体类
 */
@TableName("vehicle")
public class Vehicle extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 车牌号
     */
    private String licensePlate;

    /**
     * 车辆类型
     */
    private String vehicleType;

    /**
     * 车辆规格
     */
    private String vehicleSpec;

    /**
     * 载重量（吨）
     */
    private BigDecimal loadCapacity;

    /**
     * 容积（立方米）
     */
    private BigDecimal volumeCapacity;

    /**
     * 状态：0-停用，1-可用，2-维修中
     */
    private Integer status;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 创建人ID
     */
    private Long createUserId;

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVehicleSpec() {
        return vehicleSpec;
    }

    public void setVehicleSpec(String vehicleSpec) {
        this.vehicleSpec = vehicleSpec;
    }

    public BigDecimal getLoadCapacity() {
        return loadCapacity;
    }

    public void setLoadCapacity(BigDecimal loadCapacity) {
        this.loadCapacity = loadCapacity;
    }

    public BigDecimal getVolumeCapacity() {
        return volumeCapacity;
    }

    public void setVolumeCapacity(BigDecimal volumeCapacity) {
        this.volumeCapacity = volumeCapacity;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }
}