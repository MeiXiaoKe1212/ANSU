package com.ansu.api.service.impl;

import cn.hutool.core.util.StrUtil;
import com.ansu.api.domain.dto.LoginRequest;
import com.ansu.api.domain.dto.RegisterRequest;
import com.ansu.api.domain.entity.SysUser;
import com.ansu.api.domain.entity.Tenant;
import com.ansu.api.mapper.TenantMapper;
import com.ansu.api.mapper.UserMapper;
import com.ansu.api.service.UserService;
import com.ansu.api.util.JwtUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类（简化版）
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TenantMapper tenantMapper;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Override
    public String login(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        // 解析用户名，支持 username@tenantCode 格式
        String actualUsername = username;
        String tenantCode = "DEFAULT"; // 默认租户

        if (username.contains("@")) {
            String[] parts = username.split("@");
            if (parts.length == 2) {
                actualUsername = parts[0];
                tenantCode = parts[1];
            }
        }

        // 查找租户
        LambdaQueryWrapper<Tenant> tenantWrapper = new LambdaQueryWrapper<>();
        tenantWrapper.eq(Tenant::getTenantCode, tenantCode)
                    .eq(Tenant::getStatus, 1);
        Tenant tenant = tenantMapper.selectOne(tenantWrapper);

        if (tenant == null) {
            throw new RuntimeException("租户不存在或已禁用");
        }

        // 查找用户
        SysUser user = findByUsernameAndTenantId(actualUsername, tenant.getId());
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new RuntimeException("用户已被禁用");
        }

        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 生成JWT token，包含租户信息
        return jwtUtil.generateToken(user.getUsername(), user.getId(), user.getTenantId());
    }
    
    @Override
    public SysUser register(RegisterRequest registerRequest) {
        // 检查密码确认
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new RuntimeException("两次输入的密码不一致");
        }

        // 解析租户信息，默认使用DEFAULT租户
        String tenantCode = StrUtil.isNotBlank(registerRequest.getTenantCode()) ?
                           registerRequest.getTenantCode() : "DEFAULT";

        // 查找租户
        LambdaQueryWrapper<Tenant> tenantWrapper = new LambdaQueryWrapper<>();
        tenantWrapper.eq(Tenant::getTenantCode, tenantCode)
                    .eq(Tenant::getStatus, 1);
        Tenant tenant = tenantMapper.selectOne(tenantWrapper);

        if (tenant == null) {
            throw new RuntimeException("租户不存在或已禁用");
        }

        // 检查用户名在租户内是否已存在
        if (existsByUsernameAndTenantId(registerRequest.getUsername(), tenant.getId())) {
            throw new RuntimeException("用户名已存在");
        }

        // 创建新用户
        SysUser user = new SysUser();
        user.setTenantId(tenant.getId());
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setPhone(registerRequest.getPhone());
        user.setRealName(registerRequest.getRealName());
        user.setStatus(1); // 默认启用

        // 保存用户
        userMapper.insert(user);

        return user;
    }
    
    @Override
    public SysUser findByUsernameAndTenantId(String username, Long tenantId) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username)
               .eq(SysUser::getTenantId, tenantId);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public SysUser findById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public boolean existsByUsernameAndTenantId(String username, Long tenantId) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username)
               .eq(SysUser::getTenantId, tenantId);
        return userMapper.selectCount(wrapper) > 0;
    }

    @Override
    public String refreshToken(String oldToken) {
        try {
            // 验证旧token
            if (!jwtUtil.validateToken(oldToken)) {
                throw new RuntimeException("Token无效");
            }

            // 从旧token中获取用户信息
            String username = jwtUtil.getUsernameFromToken(oldToken);
            Long userId = jwtUtil.getUserIdFromToken(oldToken);
            Long tenantId = jwtUtil.getTenantIdFromToken(oldToken);

            // 验证用户是否仍然有效
            SysUser user = findById(userId);
            if (user == null || user.getStatus() == 0) {
                throw new RuntimeException("用户不存在或已被禁用");
            }

            // 生成新token
            return jwtUtil.generateToken(username, userId, tenantId);

        } catch (Exception e) {
            throw new RuntimeException("Token刷新失败: " + e.getMessage());
        }
    }
    
}
