package com.ansu.api.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.ansu.api.domain.dto.*;
import com.ansu.api.domain.entity.LoginAttempt;
import com.ansu.api.domain.entity.PasswordResetToken;
import com.ansu.api.domain.entity.SysUser;
import com.ansu.api.mapper.LoginAttemptMapper;
import com.ansu.api.mapper.PasswordResetTokenMapper;
import com.ansu.api.mapper.UserMapper;
import com.ansu.api.service.EmailService;
import com.ansu.api.service.UserService;
import com.ansu.api.util.JwtUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordResetTokenMapper passwordResetTokenMapper;

    @Autowired
    private LoginAttemptMapper loginAttemptMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 最大登录失败次数
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    // 锁定时间（分钟）
    private static final int LOCKOUT_DURATION_MINUTES = 30;
    
    @Override
    public String login(LoginRequest loginRequest, HttpServletRequest request) {
        String username = loginRequest.getUsername();
        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");

        try {
            // 检查用户是否被锁定
            if (isUserLocked(username)) {
                recordLoginAttempt(username, ipAddress, false, "账户被锁定", userAgent);
                throw new RuntimeException("账户已被锁定，请稍后再试");
            }

            // 查找用户
            SysUser user = findByUsername(username);
            if (user == null) {
                recordLoginAttempt(username, ipAddress, false, "用户不存在", userAgent);
                throw new RuntimeException("用户名或密码错误");
            }

            // 检查用户状态
            if (user.getStatus() == 0) {
                recordLoginAttempt(username, ipAddress, false, "用户被禁用", userAgent);
                throw new RuntimeException("用户已被禁用");
            }

            // 验证密码
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                recordLoginAttempt(username, ipAddress, false, "密码错误", userAgent);
                throw new RuntimeException("用户名或密码错误");
            }

            // 登录成功，记录成功的登录尝试
            recordLoginAttempt(username, ipAddress, true, null, userAgent);

            // 生成JWT token
            return jwtUtil.generateToken(user.getUsername(), user.getId());

        } catch (RuntimeException e) {
            // 如果不是锁定相关的异常，记录失败尝试
            if (!e.getMessage().contains("锁定")) {
                recordLoginAttempt(username, ipAddress, false, e.getMessage(), userAgent);
            }
            throw e;
        }
    }
    
    @Override
    public SysUser register(RegisterRequest registerRequest) {
        // 检查密码确认
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new RuntimeException("两次输入的密码不一致");
        }
        
        // 检查用户名是否已存在
        if (existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (StrUtil.isNotBlank(registerRequest.getEmail()) && existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("邮箱已被注册");
        }
        
        // 创建新用户
        SysUser user = new SysUser();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setPhone(registerRequest.getPhone());
        user.setRealName(registerRequest.getRealName());
        user.setStatus(1); // 默认启用
        
        // 保存用户
        userMapper.insert(user);

        // 发送欢迎邮件
        if (StrUtil.isNotBlank(user.getEmail())) {
            try {
                emailService.sendWelcomeEmail(user.getEmail(), user.getUsername());
            } catch (Exception e) {
                // 邮件发送失败不影响注册流程
                System.err.println("发送欢迎邮件失败: " + e.getMessage());
            }
        }

        return user;
    }
    
    @Override
    public SysUser findByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        return userMapper.selectOne(wrapper);
    }
    
    @Override
    public SysUser findById(Long id) {
        return userMapper.selectById(id);
    }
    
    @Override
    public boolean existsByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        return userMapper.selectCount(wrapper) > 0;
    }
    
    @Override
    public SysUser findByEmail(String email) {
        if (StrUtil.isBlank(email)) {
            return null;
        }
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getEmail, email);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public boolean existsByEmail(String email) {
        if (StrUtil.isBlank(email)) {
            return false;
        }
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getEmail, email);
        return userMapper.selectCount(wrapper) > 0;
    }

    @Override
    public void sendPasswordResetEmail(ForgotPasswordRequest request) {
        SysUser user = findByEmail(request.getEmail());
        if (user == null) {
            // 为了安全，即使用户不存在也不提示具体错误
            throw new RuntimeException("如果该邮箱已注册，您将收到密码重置邮件");
        }

        // 生成重置令牌
        String resetToken = IdUtil.simpleUUID();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(30); // 30分钟后过期

        // 保存重置令牌
        PasswordResetToken passwordResetToken = new PasswordResetToken(user.getId(), resetToken, expiryDate);
        passwordResetTokenMapper.insert(passwordResetToken);

        // 发送重置邮件
        emailService.sendPasswordResetEmail(request.getEmail(), resetToken);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        // 检查密码确认
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("两次输入的密码不一致");
        }

        // 查找重置令牌
        LambdaQueryWrapper<PasswordResetToken> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PasswordResetToken::getToken, request.getToken())
               .eq(PasswordResetToken::getUsed, 0);
        PasswordResetToken resetToken = passwordResetTokenMapper.selectOne(wrapper);

        if (resetToken == null) {
            throw new RuntimeException("无效的重置令牌");
        }

        if (resetToken.isExpired()) {
            throw new RuntimeException("重置令牌已过期");
        }

        // 更新用户密码
        SysUser user = findById(resetToken.getUserId());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userMapper.updateById(user);

        // 标记令牌为已使用
        resetToken.setUsed(1);
        passwordResetTokenMapper.updateById(resetToken);
    }

    @Override
    public SysUser updateProfile(Long userId, UpdateProfileRequest request) {
        SysUser user = findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查邮箱是否被其他用户使用
        if (StrUtil.isNotBlank(request.getEmail()) && !request.getEmail().equals(user.getEmail())) {
            SysUser existingUser = findByEmail(request.getEmail());
            if (existingUser != null && !existingUser.getId().equals(userId)) {
                throw new RuntimeException("邮箱已被其他用户使用");
            }
        }

        // 更新用户信息
        if (StrUtil.isNotBlank(request.getEmail())) {
            user.setEmail(request.getEmail());
        }
        if (StrUtil.isNotBlank(request.getPhone())) {
            user.setPhone(request.getPhone());
        }
        if (StrUtil.isNotBlank(request.getRealName())) {
            user.setRealName(request.getRealName());
        }
        if (StrUtil.isNotBlank(request.getAvatar())) {
            user.setAvatar(request.getAvatar());
        }

        userMapper.updateById(user);
        return user;
    }

    @Override
    public boolean isUserLocked(String username) {
        LocalDateTime lockoutTime = LocalDateTime.now().minusMinutes(LOCKOUT_DURATION_MINUTES);

        LambdaQueryWrapper<LoginAttempt> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LoginAttempt::getUsername, username)
               .eq(LoginAttempt::getSuccess, 0)
               .gt(LoginAttempt::getCreateTime, lockoutTime)
               .orderByDesc(LoginAttempt::getCreateTime);

        Long failedAttempts = loginAttemptMapper.selectCount(wrapper);
        return failedAttempts >= MAX_LOGIN_ATTEMPTS;
    }

    @Override
    public void recordLoginAttempt(String username, String ipAddress, boolean success, String failureReason, String userAgent) {
        LoginAttempt attempt = new LoginAttempt();
        attempt.setUsername(username);
        attempt.setIpAddress(ipAddress);
        attempt.setSuccess(success ? 1 : 0);
        attempt.setFailureReason(failureReason);
        attempt.setUserAgent(userAgent);

        loginAttemptMapper.insert(attempt);
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (StrUtil.isNotBlank(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (StrUtil.isNotBlank(xRealIp)) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}
