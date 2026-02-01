package com.ansu.api.domain.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 重置密码请求DTO
 */
public class ResetPasswordRequest {
    
    @NotBlank(message = "重置令牌不能为空")
    private String token;
    
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String newPassword;
    
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
    
    public ResetPasswordRequest() {}
    
    public ResetPasswordRequest(String token, String newPassword, String confirmPassword) {
        this.token = token;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getNewPassword() {
        return newPassword;
    }
    
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    
    public String getConfirmPassword() {
        return confirmPassword;
    }
    
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    
    @Override
    public String toString() {
        return "ResetPasswordRequest{" +
                "token='" + token + '\'' +
                ", newPassword='[PROTECTED]'" +
                ", confirmPassword='[PROTECTED]'" +
                '}';
    }
}
