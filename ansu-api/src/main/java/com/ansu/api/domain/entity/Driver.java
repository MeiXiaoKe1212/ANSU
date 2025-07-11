package com.ansu.api.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 司机实体类
 */
@TableName("driver")
public class Driver extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 司机姓名
     */
    private String name;

    /**
     * 司机电话
     */
    private String phone;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 驾驶证号
     */
    private String licenseNumber;

    /**
     * 驾驶证类型
     */
    private String licenseType;

    /**
     * 状态：0-离职，1-在职
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
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