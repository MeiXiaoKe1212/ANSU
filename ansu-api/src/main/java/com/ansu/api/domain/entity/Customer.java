package com.ansu.api.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 客户实体类
 */
@TableName("customer")
public class Customer extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 企业名称
     */
    private String companyName;

    /**
     * 联系人姓名
     */
    private String contactPerson;

    /**
     * 联系人电话
     */
    private String contactPhone;

    /**
     * 企业地址
     */
    private String address;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 信用等级：A-优秀，B-良好，C-一般，D-差
     */
    private String creditLevel;

    /**
     * 备注
     */
    private String remarks;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreditLevel() {
        return creditLevel;
    }

    public void setCreditLevel(String creditLevel) {
        this.creditLevel = creditLevel;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}