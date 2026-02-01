package com.ansu.api.service.impl;

import com.ansu.api.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 邮件服务实现类
 * 注意：这是一个简化的实现，实际生产环境中应该集成真实的邮件服务
 */
@Service
public class EmailServiceImpl implements EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    
    @Override
    public void sendPasswordResetEmail(String to, String resetToken) {
        // 在实际项目中，这里应该发送真实的邮件
        // 可以集成阿里云邮件服务、腾讯云邮件服务等
        
        String resetUrl = "http://localhost:5173/reset-password?token=" + resetToken;
        String emailContent = String.format(
            "您好，\n\n" +
            "您请求重置密码。请点击以下链接重置您的密码：\n" +
            "%s\n\n" +
            "此链接将在30分钟后过期。\n\n" +
            "如果您没有请求重置密码，请忽略此邮件。\n\n" +
            "ANSU系统",
            resetUrl
        );
        
        // 模拟发送邮件
        logger.info("发送密码重置邮件到: {}", to);
        logger.info("邮件内容: {}", emailContent);
        logger.info("重置链接: {}", resetUrl);
        
        // 在控制台输出，方便开发测试
        System.out.println("=== 密码重置邮件 ===");
        System.out.println("收件人: " + to);
        System.out.println("重置链接: " + resetUrl);
        System.out.println("==================");
    }
    
    @Override
    public void sendWelcomeEmail(String to, String username) {
        String emailContent = String.format(
            "亲爱的 %s，\n\n" +
            "欢迎加入ANSU系统！\n\n" +
            "您的账户已成功创建。您现在可以登录系统开始使用各项功能。\n\n" +
            "如有任何问题，请联系我们的客服团队。\n\n" +
            "祝您使用愉快！\n\n" +
            "ANSU团队",
            username
        );
        
        logger.info("发送欢迎邮件到: {}", to);
        logger.info("邮件内容: {}", emailContent);
        
        System.out.println("=== 欢迎邮件 ===");
        System.out.println("收件人: " + to);
        System.out.println("用户名: " + username);
        System.out.println("===============");
    }
    
    @Override
    public void sendVerificationEmail(String to, String verificationToken) {
        String verificationUrl = "http://localhost:5173/verify-email?token=" + verificationToken;
        String emailContent = String.format(
            "您好，\n\n" +
            "感谢您注册ANSU系统。请点击以下链接验证您的邮箱地址：\n" +
            "%s\n\n" +
            "此链接将在24小时后过期。\n\n" +
            "ANSU系统",
            verificationUrl
        );
        
        logger.info("发送邮箱验证邮件到: {}", to);
        logger.info("邮件内容: {}", emailContent);
        
        System.out.println("=== 邮箱验证邮件 ===");
        System.out.println("收件人: " + to);
        System.out.println("验证链接: " + verificationUrl);
        System.out.println("==================");
    }
}
