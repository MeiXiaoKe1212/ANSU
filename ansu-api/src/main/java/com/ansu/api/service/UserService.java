package com.ansu.api.service;

import com.ansu.api.domain.dto.LoginRequest;
import com.ansu.api.domain.dto.RegisterRequest;
import com.ansu.api.domain.entity.SysUser;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户登录
     */
    Map<String, Object> login(LoginRequest loginRequest, HttpServletRequest request);
    
    /**
     * 用户注册
     */
    SysUser register(RegisterRequest registerRequest);
    
    /**
     * 刷新Token
     */
    Map<String, Object> refreshToken(String refreshToken);
    
    /**
     * 根据用户名查找用户
     */
    SysUser findByUsername(String username);
    
    /**
     * 根据ID查找用户
     */
    SysUser findById(Long id);
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 根据邮箱查找用户
     */
    SysUser findByEmail(String email);
}
