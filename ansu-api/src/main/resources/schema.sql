-- 创建数据库
CREATE DATABASE IF NOT EXISTS ansu_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE ansu_db;

-- 租户表
CREATE TABLE IF NOT EXISTS tenant (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '租户ID',
    tenant_code VARCHAR(50) NOT NULL UNIQUE COMMENT '租户编码',
    tenant_name VARCHAR(100) NOT NULL COMMENT '租户名称',
    contact_name VARCHAR(50) COMMENT '联系人姓名',
    contact_phone VARCHAR(20) COMMENT '联系人电话',
    contact_email VARCHAR(100) COMMENT '联系人邮箱',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    expire_time DATETIME COMMENT '过期时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_tenant_code (tenant_code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='租户表';

-- 用户表（多租户）
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    real_name VARCHAR(50) COMMENT '真实姓名',
    avatar VARCHAR(200) COMMENT '头像URL',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_tenant_username (tenant_id, username) COMMENT '租户内用户名唯一',
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_phone (phone),
    FOREIGN KEY (tenant_id) REFERENCES tenant(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

-- 订单表（多租户）
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    order_no VARCHAR(50) NOT NULL COMMENT '订单编号',
    customer_name VARCHAR(100) NOT NULL COMMENT '客户姓名',
    customer_phone VARCHAR(20) COMMENT '客户电话',
    pickup_address TEXT NOT NULL COMMENT '取货地址',
    delivery_address TEXT NOT NULL COMMENT '送货地址',
    goods_name VARCHAR(200) COMMENT '货物名称',
    goods_weight DECIMAL(10,2) COMMENT '货物重量(kg)',
    goods_volume DECIMAL(10,2) COMMENT '货物体积(m³)',
    transport_fee DECIMAL(10,2) NOT NULL COMMENT '运输费用',
    status TINYINT DEFAULT 1 COMMENT '订单状态：1-待接单，2-已接单，3-运输中，4-已完成，5-已取消',
    remark TEXT COMMENT '备注',
    created_by BIGINT COMMENT '创建人ID',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_tenant_order_no (tenant_id, order_no) COMMENT '租户内订单号唯一',
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_order_no (order_no),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time),
    FOREIGN KEY (tenant_id) REFERENCES tenant(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES sys_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- 插入默认租户
INSERT INTO tenant (tenant_code, tenant_name, contact_name, contact_email, status)
VALUES ('DEFAULT', '默认租户', '系统管理员', 'admin@ansu.com', 1)
ON DUPLICATE KEY UPDATE tenant_name = tenant_name;

-- 插入演示租户
INSERT INTO tenant (tenant_code, tenant_name, contact_name, contact_email, status)
VALUES
('DEMO001', '演示公司A', '张三', 'demo1@ansu.com', 1),
('DEMO002', '演示公司B', '李四', 'demo2@ansu.com', 1)
ON DUPLICATE KEY UPDATE tenant_name = tenant_name;

-- 插入默认管理员用户（密码：admin123）
INSERT INTO sys_user (tenant_id, username, password, email, real_name, status)
VALUES (1, 'admin', '$2a$10$7JB720yubVSOfvVWbfXXSOjSWANqPS.rUw6T.GdHK2pqUv.dJeAIa', 'admin@ansu.com', '系统管理员', 1)
ON DUPLICATE KEY UPDATE username = username;

-- 插入演示用户
INSERT INTO sys_user (tenant_id, username, password, email, real_name, status)
VALUES
(2, 'demo1', '$2a$10$7JB720yubVSOfvVWbfXXSOjSWANqPS.rUw6T.GdHK2pqUv.dJeAIa', 'demo1@ansu.com', '演示用户1', 1),
(3, 'demo2', '$2a$10$7JB720yubVSOfvVWbfXXSOjSWANqPS.rUw6T.GdHK2pqUv.dJeAIa', 'demo2@ansu.com', '演示用户2', 1)
ON DUPLICATE KEY UPDATE username = username;

-- 插入演示订单数据
INSERT INTO orders (tenant_id, order_no, customer_name, customer_phone, pickup_address, delivery_address, goods_name, goods_weight, transport_fee, status, created_by)
VALUES
(2, 'ORD20241201001', '王五', '13800138001', '北京市朝阳区建国门外大街1号', '上海市浦东新区陆家嘴环路1000号', '电子产品', 10.5, 150.00, 1, 2),
(2, 'ORD20241201002', '赵六', '13800138002', '北京市海淀区中关村大街1号', '广州市天河区珠江新城1号', '服装', 5.2, 120.00, 2, 2),
(3, 'ORD20241201003', '孙七', '13800138003', '上海市黄浦区南京东路1号', '深圳市南山区科技园1号', '数码设备', 8.8, 200.00, 1, 3),
(3, 'ORD20241201004', '周八', '13800138004', '广州市越秀区中山五路1号', '杭州市西湖区文三路1号', '办公用品', 15.0, 180.00, 3, 3)
ON DUPLICATE KEY UPDATE order_no = order_no;