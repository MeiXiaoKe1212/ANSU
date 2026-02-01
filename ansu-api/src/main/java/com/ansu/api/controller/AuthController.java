package com.ansu.api.controller;

import com.ansu.api.domain.dto.*;
import com.ansu.api.domain.entity.SysUser;
import com.ansu.api.service.UserService;
import com.ansu.api.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // 允许跨域，生产环境应该配置具体域名
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
            String token = userService.login(loginRequest, request);
            SysUser user = userService.findByUsername(loginRequest.getUsername());

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", user);

            return ApiResponse.success("登录成功", data);
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
            
            return ApiResponse.success(user);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 检查用户名是否可用
     */
    @GetMapping("/check-username")
    public ApiResponse<Boolean> checkUsername(@RequestParam String username) {
        boolean exists = userService.existsByUsername(username);
        return ApiResponse.success(!exists); // 返回是否可用
    }
    
    /**
     * 检查邮箱是否可用
     */
    @GetMapping("/check-email")
    public ApiResponse<Boolean> checkEmail(@RequestParam String email) {
        boolean exists = userService.existsByEmail(email);
        return ApiResponse.success(!exists); // 返回是否可用
    }
    
    /**
     * 忘记密码
     */
    @PostMapping("/forgot-password")
    public ApiResponse<Void> forgotPassword(@Validated @RequestBody ForgotPasswordRequest request) {
        try {
            userService.sendPasswordResetEmail(request);
            return ApiResponse.success("如果该邮箱已注册，您将收到密码重置邮件");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 重置密码
     */
    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@Validated @RequestBody ResetPasswordRequest request) {
        try {
            userService.resetPassword(request);
            return ApiResponse.success("密码重置成功");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/profile")
    public ApiResponse<SysUser> updateProfile(@Validated @RequestBody UpdateProfileRequest request, HttpServletRequest httpRequest) {
        try {
            String token = getTokenFromRequest(httpRequest);
            if (token == null) {
                return ApiResponse.unauthorized("未提供认证令牌");
            }

            if (!jwtUtil.validateToken(token)) {
                return ApiResponse.unauthorized("认证令牌无效");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            SysUser updatedUser = userService.updateProfile(userId, request);

            return ApiResponse.success("信息更新成功", updatedUser);
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
