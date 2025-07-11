package com.ansu.api.service;

/**
 * 邮件服务接口
 */
public interface EmailService {
    
    /**
     * 发送密码重置邮件
     */
    void sendPasswordResetEmail(String to, String resetToken);
    
    /**
     * 发送欢迎邮件
     */
    void sendWelcomeEmail(String to, String username);
    
    /**
     * 发送验证邮件
     */
    void sendVerificationEmail(String to, String verificationToken);
}
