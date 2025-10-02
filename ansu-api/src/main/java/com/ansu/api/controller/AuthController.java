package com.ansu.api.controller;

import com.ansu.api.domain.dto.*;
import com.ansu.api.domain.entity.SysUser;
import com.ansu.api.service.UserService;
import com.ansu.api.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@Validated @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        try {
            Map<String, Object> result = userService.login(loginRequest, request);
            return ApiResponse.success("登录成功", result);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ApiResponse<SysUser> register(@Validated @RequestBody RegisterRequest registerRequest) {
        try {
            SysUser user = userService.register(registerRequest);
            return ApiResponse.success("注册成功", user);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 刷新Token
     */
    @PostMapping("/refresh")
    public ApiResponse<Map<String, Object>> refreshToken(@Validated @RequestBody RefreshTokenRequest refreshTokenRequest) {
        try {
            Map<String, Object> result = userService.refreshToken(refreshTokenRequest.getRefreshToken());
            return ApiResponse.success("Token刷新成功", result);
        } catch (Exception e) {
            return ApiResponse.unauthorized(e.getMessage());
        }
    }
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public ApiResponse<SysUser> getCurrentUser(HttpServletRequest request) {
        try {
            String token = getTokenFromRequest(request);
            if (token == null) {
                return ApiResponse.unauthorized("未提供认证令牌");
            }
            
            if (!jwtUtil.validateToken(token)) {
                return ApiResponse.unauthorized("认证令牌无效");
            }
            
            Long userId = jwtUtil.getUserIdFromToken(token);
            SysUser user = userService.findById(userId);
            
            if (user == null) {
                return ApiResponse.unauthorized("用户不存在");
            }
            
            // 清除密码信息
            user.setPassword(null);
            
            return ApiResponse.success(user);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request) {
        try {
            // 这里可以实现token黑名单机制
            // 目前简单返回成功，前端清除本地token即可
            return ApiResponse.success("登出成功", null);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 从请求中获取token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
