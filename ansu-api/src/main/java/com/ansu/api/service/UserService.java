package com.ansu.api.service;

import com.ansu.api.domain.dto.LoginRequest;
import com.ansu.api.domain.dto.RegisterRequest;
import com.ansu.api.domain.entity.SysUser;

/**
 * 用户服务接口（简化版）
 */
public interface UserService {

    /**
     * 用户登录
     */
    String login(LoginRequest loginRequest);

    /**
     * 用户注册
     */
    SysUser register(RegisterRequest registerRequest);

    /**
     * 根据用户名和租户ID查找用户
     */
    SysUser findByUsernameAndTenantId(String username, Long tenantId);

    /**
     * 根据用户ID查找用户
     */
    SysUser findById(Long id);

    /**
     * 检查用户名在租户内是否存在
     */
    boolean existsByUsernameAndTenantId(String username, Long tenantId);

    /**
     * 刷新JWT Token
     */
    String refreshToken(String oldToken);
}
