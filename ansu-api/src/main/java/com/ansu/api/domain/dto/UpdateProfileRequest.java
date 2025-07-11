package com.ansu.api.domain.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

/**
 * 更新用户信息请求DTO
 */
public class UpdateProfileRequest {
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    private String realName;
    
    private String avatar;
    
    public UpdateProfileRequest() {}
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getRealName() {
        return realName;
    }
    
    public void setRealName(String realName) {
        this.realName = realName;
    }
    
    public String getAvatar() {
        return avatar;
    }
    
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    
    @Override
    public String toString() {
        return "UpdateProfileRequest{" +
                "email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", realName='" + realName + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}
