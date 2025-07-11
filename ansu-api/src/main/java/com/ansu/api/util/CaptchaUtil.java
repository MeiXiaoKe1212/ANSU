package com.ansu.api.util;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.util.IdUtil;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 验证码工具类
 */
@Component
public class CaptchaGenerator {
    
    // 验证码存储（生产环境建议使用Redis）
    private final ConcurrentHashMap<String, CaptchaInfo> captchaStore = new ConcurrentHashMap<>();
    
    // 定时清理过期验证码
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    public CaptchaGenerator() {
        // 每分钟清理一次过期验证码
        scheduler.scheduleAtFixedRate(this::cleanExpiredCaptcha, 1, 1, TimeUnit.MINUTES);
    }
    
    /**
     * 生成验证码
     */
    public CaptchaResult generateCaptcha() {
        // 创建线性验证码
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(120, 40, 4, 20);
        
        // 生成验证码ID
        String captchaId = IdUtil.simpleUUID();
        
        // 获取验证码文本
        String code = lineCaptcha.getCode();
        
        // 获取验证码图片Base64
        String imageBase64 = lineCaptcha.getImageBase64();
        
        // 存储验证码信息（5分钟过期）
        long expireTime = System.currentTimeMillis() + 5 * 60 * 1000;
        captchaStore.put(captchaId, new CaptchaInfo(code, expireTime));
        
        return new CaptchaResult(captchaId, imageBase64);
    }
    
    /**
     * 验证验证码
     */
    public boolean verifyCaptcha(String captchaId, String userInput) {
        if (captchaId == null || userInput == null) {
            return false;
        }
        
        CaptchaInfo captchaInfo = captchaStore.get(captchaId);
        if (captchaInfo == null) {
            return false;
        }
        
        // 检查是否过期
        if (System.currentTimeMillis() > captchaInfo.getExpireTime()) {
            captchaStore.remove(captchaId);
            return false;
        }
        
        // 验证码只能使用一次
        captchaStore.remove(captchaId);
        
        // 忽略大小写比较
        return captchaInfo.getCode().equalsIgnoreCase(userInput);
    }
    
    /**
     * 清理过期验证码
     */
    private void cleanExpiredCaptcha() {
        long currentTime = System.currentTimeMillis();
        captchaStore.entrySet().removeIf(entry -> currentTime > entry.getValue().getExpireTime());
    }
    
    /**
     * 验证码信息内部类
     */
    private static class CaptchaInfo {
        private final String code;
        private final long expireTime;
        
        public CaptchaInfo(String code, long expireTime) {
            this.code = code;
            this.expireTime = expireTime;
        }
        
        public String getCode() {
            return code;
        }
        
        public long getExpireTime() {
            return expireTime;
        }
    }
    
    /**
     * 验证码结果类
     */
    public static class CaptchaResult {
        private final String captchaId;
        private final String imageBase64;
        
        public CaptchaResult(String captchaId, String imageBase64) {
            this.captchaId = captchaId;
            this.imageBase64 = imageBase64;
        }
        
        public String getCaptchaId() {
            return captchaId;
        }
        
        public String getImageBase64() {
            return imageBase64;
        }
    }
}
