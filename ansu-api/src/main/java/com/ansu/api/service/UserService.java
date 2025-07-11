package com.ansu.api.service;

import com.ansu.api.domain.dto.LoginRequest;
import com.ansu.api.domain.dto.RegisterRequest;
import com.ansu.api.domain.entity.SysUser;

/**
 * 用户服务接口
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
     * 根据用户名查找用户
     */
    SysUser findByUsername(String username);
    
    /**
     * 根据用户ID查找用户
     */
    SysUser findById(Long id);
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);
}
