package com.ansu.api.service;

import com.ansu.api.domain.dto.*;
import com.ansu.api.domain.entity.SysUser;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户登录
     */
    String login(LoginRequest loginRequest, HttpServletRequest request);

    /**
     * 用户注册
     */
    SysUser register(RegisterRequest registerRequest);

    /**
     * 根据用户名查找用户
     */
    SysUser findByUsername(String username);

    /**
     * 根据用户ID查找用户
     */
    SysUser findById(Long id);

    /**
     * 根据邮箱查找用户
     */
    SysUser findByEmail(String email);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 发送密码重置邮件
     */
    void sendPasswordResetEmail(ForgotPasswordRequest request);

    /**
     * 重置密码
     */
    void resetPassword(ResetPasswordRequest request);

    /**
     * 更新用户信息
     */
    SysUser updateProfile(Long userId, UpdateProfileRequest request);

    /**
     * 检查用户是否被锁定
     */
    boolean isUserLocked(String username);

    /**
     * 记录登录尝试
     */
    void recordLoginAttempt(String username, String ipAddress, boolean success, String failureReason, String userAgent);
}
