-- 运输中介管理系统数据库表结构

-- 用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `real_name` varchar(50) NOT NULL COMMENT '真实姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `status` tinyint DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 客户表（第三方企业）
CREATE TABLE IF NOT EXISTS `customer` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '客户ID',
  `company_name` varchar(200) NOT NULL COMMENT '企业名称',
  `contact_person` varchar(50) NOT NULL COMMENT '联系人姓名',
  `contact_phone` varchar(20) NOT NULL COMMENT '联系人电话',
  `address` varchar(500) DEFAULT NULL COMMENT '企业地址',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `credit_level` varchar(20) DEFAULT 'A' COMMENT '信用等级：A-优秀，B-良好，C-一般，D-差',
  `remarks` text COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_company_name` (`company_name`),
  KEY `idx_contact_phone` (`contact_phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户表';

-- 司机表
CREATE TABLE IF NOT EXISTS `driver` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '司机ID',
  `name` varchar(50) NOT NULL COMMENT '司机姓名',
  `phone` varchar(20) NOT NULL COMMENT '司机电话',
  `id_card` varchar(18) DEFAULT NULL COMMENT '身份证号',
  `license_number` varchar(50) DEFAULT NULL COMMENT '驾驶证号',
  `license_type` varchar(10) DEFAULT NULL COMMENT '驾驶证类型',
  `status` tinyint DEFAULT 1 COMMENT '状态：0-离职，1-在职',
  `remarks` text COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone` (`phone`),
  KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='司机表';

-- 车辆表
CREATE TABLE IF NOT EXISTS `vehicle` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '车辆ID',
  `license_plate` varchar(20) NOT NULL COMMENT '车牌号',
  `vehicle_type` varchar(50) NOT NULL COMMENT '车辆类型',
  `vehicle_spec` varchar(100) DEFAULT NULL COMMENT '车辆规格',
  `load_capacity` decimal(10,2) DEFAULT NULL COMMENT '载重量（吨）',
  `volume_capacity` decimal(10,2) DEFAULT NULL COMMENT '容积（立方米）',
  `status` tinyint DEFAULT 1 COMMENT '状态：0-停用，1-可用，2-维修中',
  `remarks` text COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_license_plate` (`license_plate`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆表';

-- 运输订单表
CREATE TABLE IF NOT EXISTS `transport_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(50) NOT NULL COMMENT '订单编号',
  `customer_id` bigint NOT NULL COMMENT '客户ID',
  `customer_company_name` varchar(200) NOT NULL COMMENT '客户企业名称',
  `customer_contact_person` varchar(50) NOT NULL COMMENT '客户联系人',
  `customer_contact_phone` varchar(20) NOT NULL COMMENT '客户联系电话',
  `start_address` varchar(500) NOT NULL COMMENT '发送地点',
  `end_address` varchar(500) NOT NULL COMMENT '目标地点',
  `cargo_name` varchar(200) NOT NULL COMMENT '货物内容',
  `cargo_weight` decimal(10,2) DEFAULT NULL COMMENT '货物重量（吨）',
  `cargo_volume` decimal(10,2) DEFAULT NULL COMMENT '货物体积（立方米）',
  `vehicle_id` bigint DEFAULT NULL COMMENT '车辆ID',
  `license_plate` varchar(20) DEFAULT NULL COMMENT '车牌号',
  `vehicle_type` varchar(50) DEFAULT NULL COMMENT '车辆类型',
  `vehicle_spec` varchar(100) DEFAULT NULL COMMENT '车辆规格',
  `driver_id` bigint DEFAULT NULL COMMENT '司机ID',
  `driver_name` varchar(50) DEFAULT NULL COMMENT '司机姓名',
  `driver_phone` varchar(20) DEFAULT NULL COMMENT '司机电话',
  `transport_status` varchar(20) DEFAULT 'CREATED' COMMENT '运输状态：CREATED-已创建，DEPARTED-已出发，TRANSPORTING-运输中，EXCEPTION-异常，DELIVERED-已送达',
  `payment_status` varchar(20) DEFAULT 'UNPAID' COMMENT '款项状态：UNPAID-未付款，PREPAID-已预付费，PAID-已结款',
  `is_outsourced` tinyint DEFAULT 0 COMMENT '是否外包：0-自有车辆，1-第三方平台',
  `third_party_platform` varchar(100) DEFAULT NULL COMMENT '第三方平台名称',
  `third_party_order_id` varchar(100) DEFAULT NULL COMMENT '第三方订单号',
  `quoted_price` decimal(10,2) DEFAULT NULL COMMENT '报价金额',
  `actual_price` decimal(10,2) DEFAULT NULL COMMENT '实际收款金额',
  `total_cost` decimal(10,2) DEFAULT 0.00 COMMENT '总成本',
  `profit` decimal(10,2) DEFAULT 0.00 COMMENT '利润',
  `profit_rate` decimal(5,2) DEFAULT 0.00 COMMENT '利润率（%）',
  `departure_time` datetime DEFAULT NULL COMMENT '出发时间',
  `arrival_time` datetime DEFAULT NULL COMMENT '到达时间',
  `remarks` text COMMENT '备注',
  `create_user_id` bigint NOT NULL COMMENT '创建人ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_vehicle_id` (`vehicle_id`),
  KEY `idx_driver_id` (`driver_id`),
  KEY `idx_transport_status` (`transport_status`),
  KEY `idx_payment_status` (`payment_status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运输订单表';

-- 订单成本表
CREATE TABLE IF NOT EXISTS `order_cost` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '成本ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `cost_type` varchar(50) NOT NULL COMMENT '成本类型：FUEL-加油费，TOLL-过路费，FINE-违章费，MAINTENANCE-维修费，OUTSOURCE-外包费，OTHER-其他',
  `cost_name` varchar(100) NOT NULL COMMENT '成本名称',
  `amount` decimal(10,2) NOT NULL COMMENT '金额',
  `cost_date` date NOT NULL COMMENT '发生日期',
  `description` varchar(500) DEFAULT NULL COMMENT '费用描述',
  `receipt_url` varchar(500) DEFAULT NULL COMMENT '凭证图片URL',
  `create_user_id` bigint NOT NULL COMMENT '创建人ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_cost_type` (`cost_type`),
  KEY `idx_cost_date` (`cost_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单成本表';

-- 订单状态日志表
CREATE TABLE IF NOT EXISTS `order_status_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `status_type` varchar(20) NOT NULL COMMENT '状态类型：TRANSPORT-运输状态，PAYMENT-款项状态',
  `old_status` varchar(20) DEFAULT NULL COMMENT '原状态',
  `new_status` varchar(20) NOT NULL COMMENT '新状态',
  `change_reason` varchar(500) DEFAULT NULL COMMENT '变更原因',
  `operator_id` bigint NOT NULL COMMENT '操作人ID',
  `operator_name` varchar(50) NOT NULL COMMENT '操作人姓名',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_status_type` (`status_type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单状态日志表';

-- 订单事件表
CREATE TABLE IF NOT EXISTS `order_event` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '事件ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `event_type` varchar(50) NOT NULL COMMENT '事件类型：DRIVER_REST-司机休息，ACCIDENT-交通事故，BREAKDOWN-车辆故障，DELAY-延误，WEATHER-天气影响，OTHER-其他',
  `event_title` varchar(100) NOT NULL COMMENT '事件标题',
  `event_description` text COMMENT '事件描述',
  `event_time` datetime NOT NULL COMMENT '事件发生时间',
  `location` varchar(200) DEFAULT NULL COMMENT '事件发生地点',
  `impact_level` varchar(20) DEFAULT 'LOW' COMMENT '影响程度：LOW-轻微，MEDIUM-中等，HIGH-严重',
  `is_resolved` tinyint DEFAULT 0 COMMENT '是否已解决：0-未解决，1-已解决',
  `resolution_time` datetime DEFAULT NULL COMMENT '解决时间',
  `resolution_description` text COMMENT '解决方案描述',
  `create_user_id` bigint NOT NULL COMMENT '创建人ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_event_type` (`event_type`),
  KEY `idx_event_time` (`event_time`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单事件表';

-- 插入默认用户数据
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `phone`, `status`) VALUES
('admin', '$2a$10$7JB720yubVSOfvVWdBYoOeymNWjQy2YcHqZH/Vb2P5eZIDtrcDfIu', '管理员', '13800138000', 1),
('user1', '$2a$10$7JB720yubVSOfvVWdBYoOeymNWjQy2YcHqZH/Vb2P5eZIDtrcDfIu', '用户1', '13800138001', 1),
('user2', '$2a$10$7JB720yubVSOfvVWdBYoOeymNWjQy2YcHqZH/Vb2P5eZIDtrcDfIu', '用户2', '13800138002', 1)
ON DUPLICATE KEY UPDATE `username` = VALUES(`username`);