package com.ansu.api.domain.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;

/**
 * 登录尝试记录实体类
 */
@TableName("login_attempt")
public class LoginAttempt {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;
    
    private String ipAddress;
    
    private Integer success; // 0-失败，1-成功
    
    private String userAgent;
    
    private String failureReason;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    public LoginAttempt() {}
    
    public LoginAttempt(String username, String ipAddress, Integer success) {
        this.username = username;
        this.ipAddress = ipAddress;
        this.success = success;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public Integer getSuccess() {
        return success;
    }
    
    public void setSuccess(Integer success) {
        this.success = success;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    public String getFailureReason() {
        return failureReason;
    }
    
    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    @Override
    public String toString() {
        return "LoginAttempt{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", success=" + success +
                ", userAgent='" + userAgent + '\'' +
                ", failureReason='" + failureReason + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
