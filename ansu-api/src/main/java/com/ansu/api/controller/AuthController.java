package com.ansu.api.controller;

import com.ansu.api.domain.dto.ApiResponse;
import com.ansu.api.domain.dto.LoginRequest;
import com.ansu.api.domain.dto.RegisterRequest;
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
 * 认证控制器（简化版）
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
    public ApiResponse<Map<String, Object>> login(@Validated @RequestBody LoginRequest loginRequest) {
        try {
            String token = userService.login(loginRequest);

            // 从token中获取用户信息
            Long userId = jwtUtil.getUserIdFromToken(token);
            Long tenantId = jwtUtil.getTenantIdFromToken(token);
            SysUser user = userService.findById(userId);

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", user);
            data.put("tenantId", tenantId);

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
    public ApiResponse<Map<String, Object>> getCurrentUser(HttpServletRequest request) {
        try {
            String token = getTokenFromRequest(request);
            if (token == null) {
                return ApiResponse.unauthorized("未提供认证令牌");
            }

            if (!jwtUtil.validateToken(token)) {
                return ApiResponse.unauthorized("认证令牌无效");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            Long tenantId = jwtUtil.getTenantIdFromToken(token);
            SysUser user = userService.findById(userId);

            if (user == null) {
                return ApiResponse.unauthorized("用户不存在");
            }

            Map<String, Object> data = new HashMap<>();
            data.put("user", user);
            data.put("tenantId", tenantId);

            return ApiResponse.success(data);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 刷新Token
     */
    @PostMapping("/refresh-token")
    public ApiResponse<Map<String, Object>> refreshToken(HttpServletRequest request) {
        try {
            String oldToken = getTokenFromRequest(request);
            if (oldToken == null) {
                return ApiResponse.unauthorized("未提供认证令牌");
            }

            String newToken = userService.refreshToken(oldToken);

            Map<String, Object> data = new HashMap<>();
            data.put("token", newToken);

            return ApiResponse.success("Token刷新成功", data);
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
