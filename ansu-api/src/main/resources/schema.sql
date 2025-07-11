-- 创建数据库
CREATE DATABASE IF NOT EXISTS ansu_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE ansu_db;

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    real_name VARCHAR(50) COMMENT '真实姓名',
    avatar VARCHAR(200) COMMENT '头像URL',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

-- 密码重置令牌表
CREATE TABLE IF NOT EXISTS password_reset_token (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '令牌ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    token VARCHAR(100) NOT NULL UNIQUE COMMENT '重置令牌',
    expiry_date DATETIME NOT NULL COMMENT '过期时间',
    used TINYINT DEFAULT 0 COMMENT '是否已使用：0-未使用，1-已使用',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_token (token),
    INDEX idx_expiry_date (expiry_date),
    FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='密码重置令牌表';

-- 登录尝试记录表
CREATE TABLE IF NOT EXISTS login_attempt (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    ip_address VARCHAR(45) COMMENT 'IP地址',
    success TINYINT NOT NULL COMMENT '是否成功：0-失败，1-成功',
    user_agent TEXT COMMENT '用户代理',
    failure_reason VARCHAR(200) COMMENT '失败原因',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_username (username),
    INDEX idx_ip_address (ip_address),
    INDEX idx_success (success),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='登录尝试记录表';

-- 插入默认管理员用户（密码：admin123）
INSERT INTO sys_user (username, password, email, real_name, status)
VALUES ('admin', '$2a$10$7JB720yubVSOfvVWbfXXSOjSWANqPS.rUw6T.GdHK2pqUv.dJeAIa', 'admin@ansu.com', '系统管理员', 1)
ON DUPLICATE KEY UPDATE username = username;