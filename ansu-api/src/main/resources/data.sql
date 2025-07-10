-- 示例数据插入脚本

-- 插入示例客户数据
INSERT INTO `customer` (`company_name`, `contact_person`, `contact_phone`, `address`, `credit_level`, `remarks`) VALUES
('北京物流有限公司', '张经理', '13800138001', '北京市朝阳区建国路88号', 'A', '长期合作客户'),
('上海货运公司', '李总', '13800138002', '上海市浦东新区陆家嘴金融区', 'A', '信誉良好'),
('广州运输集团', '王主管', '13800138003', '广州市天河区珠江新城', 'B', '新客户'),
('深圳快递服务', '陈经理', '13800138004', '深圳市南山区科技园', 'A', '电商物流客户'),
('杭州贸易公司', '刘总监', '13800138005', '杭州市西湖区文三路', 'B', '季节性客户')
ON DUPLICATE KEY UPDATE `company_name` = VALUES(`company_name`);

-- 插入示例司机数据
INSERT INTO `driver` (`name`, `phone`, `id_card`, `license_number`, `license_type`, `status`, `remarks`) VALUES
('张师傅', '13900139001', '110101198001011234', 'B2001234567', 'B2', 1, '经验丰富，驾驶技术好'),
('李师傅', '13900139002', '110101198502022345', 'B2002345678', 'B2', 1, '熟悉华北地区路线'),
('王师傅', '13900139003', '110101199003033456', 'B2003456789', 'B2', 1, '新入职司机'),
('陈师傅', '13900139004', '110101198704044567', 'B2004567890', 'B2', 1, '专跑长途'),
('刘师傅', '13900139005', '110101199205055678', 'B2005678901', 'B2', 0, '已离职')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- 插入示例车辆数据
INSERT INTO `vehicle` (`license_plate`, `vehicle_type`, `vehicle_spec`, `load_capacity`, `volume_capacity`, `status`, `remarks`) VALUES
('京A12345', '厢式货车', '4.2米', 2.00, 15.00, 1, '状态良好'),
('京B23456', '平板车', '6.8米', 5.00, 25.00, 1, '适合大件运输'),
('京C34567', '厢式货车', '7.6米', 8.00, 35.00, 1, '长途运输专用'),
('京D45678', '小型货车', '3.2米', 1.50, 8.00, 1, '市内配送'),
('京E56789', '厢式货车', '9.6米', 12.00, 50.00, 2, '维修中')
ON DUPLICATE KEY UPDATE `license_plate` = VALUES(`license_plate`);

-- 插入示例运输订单数据
INSERT INTO `transport_order` (
    `order_no`, `customer_id`, `customer_company_name`, `customer_contact_person`, `customer_contact_phone`,
    `start_address`, `end_address`, `cargo_name`, `cargo_weight`, `cargo_volume`,
    `vehicle_id`, `license_plate`, `vehicle_type`, `vehicle_spec`,
    `driver_id`, `driver_name`, `driver_phone`,
    `transport_status`, `payment_status`, `is_outsourced`,
    `quoted_price`, `actual_price`, `total_cost`, `profit`, `profit_rate`,
    `remarks`, `create_user_id`
) VALUES
(
    'TO20241210001', 1, '北京物流有限公司', '张经理', '13800138001',
    '北京市朝阳区建国路88号', '上海市浦东新区陆家嘴金融区', '电子产品', 2.50, 12.00,
    1, '京A12345', '厢式货车', '4.2米',
    1, '张师傅', '13900139001',
    'DELIVERED', 'PAID', 0,
    5000.00, 5000.00, 3200.00, 1800.00, 36.00,
    '按时送达，客户满意', 1
),
(
    'TO20241210002', 2, '上海货运公司', '李总', '13800138002',
    '上海市浦东新区陆家嘴金融区', '广州市天河区珠江新城', '服装货物', 1.80, 8.50,
    2, '京B23456', '平板车', '6.8米',
    2, '李师傅', '13900139002',
    'TRANSPORTING', 'PREPAID', 0,
    4500.00, 4500.00, 2800.00, 1700.00, 37.78,
    '运输中，预计明天到达', 1
),
(
    'TO20241210003', 3, '广州运输集团', '王主管', '13800138003',
    '广州市天河区珠江新城', '深圳市南山区科技园', '机械设备', 5.20, 20.00,
    NULL, NULL, NULL, NULL,
    NULL, NULL, NULL,
    'CREATED', 'UNPAID', 1,
    3500.00, NULL, 0.00, 0.00, 0.00,
    '需要外包给货拉拉', 1
),
(
    'TO20241210004', 4, '深圳快递服务', '陈经理', '13800138004',
    '深圳市南山区科技园', '杭州市西湖区文三路', '快递包裹', 0.80, 3.50,
    4, '京D45678', '小型货车', '3.2米',
    3, '王师傅', '13900139003',
    'DEPARTED', 'UNPAID', 0,
    2800.00, 2800.00, 1500.00, 1300.00, 46.43,
    '已出发，预计后天到达', 1
),
(
    'TO20241210005', 5, '杭州贸易公司', '刘总监', '13800138005',
    '杭州市西湖区文三路', '北京市朝阳区建国路88号', '办公用品', 1.20, 6.00,
    3, '京C34567', '厢式货车', '7.6米',
    4, '陈师傅', '13900139004',
    'EXCEPTION', 'PREPAID', 0,
    4200.00, 4200.00, 2600.00, 1600.00, 38.10,
    '路上遇到交通事故，延误2小时', 1
)
ON DUPLICATE KEY UPDATE `order_no` = VALUES(`order_no`);

-- 插入示例订单成本数据
INSERT INTO `order_cost` (`order_id`, `cost_type`, `cost_name`, `amount`, `cost_date`, `description`, `create_user_id`) VALUES
(1, 'FUEL', '加油费', 800.00, '2024-12-09', '北京到上海往返加油', 1),
(1, 'TOLL', '过路费', 1200.00, '2024-12-09', '高速公路过路费', 1),
(1, 'OTHER', '餐费住宿', 400.00, '2024-12-09', '司机餐费和住宿费', 1),
(1, 'MAINTENANCE', '车辆保养', 800.00, '2024-12-08', '出发前车辆保养', 1),

(2, 'FUEL', '加油费', 600.00, '2024-12-10', '上海到广州加油', 1),
(2, 'TOLL', '过路费', 900.00, '2024-12-10', '高速公路过路费', 1),
(2, 'OTHER', '餐费', 200.00, '2024-12-10', '司机餐费', 1),
(2, 'MAINTENANCE', '轮胎更换', 1100.00, '2024-12-09', '更换磨损轮胎', 1),

(4, 'FUEL', '加油费', 400.00, '2024-12-10', '深圳到杭州加油', 1),
(4, 'TOLL', '过路费', 700.00, '2024-12-10', '高速公路过路费', 1),
(4, 'OTHER', '停车费', 50.00, '2024-12-10', '服务区停车费', 1),
(4, 'OTHER', '餐费', 350.00, '2024-12-10', '司机餐费和住宿', 1),

(5, 'FUEL', '加油费', 500.00, '2024-12-10', '杭州到北京加油', 1),
(5, 'TOLL', '过路费', 800.00, '2024-12-10', '高速公路过路费', 1),
(5, 'FINE', '违章罚款', 200.00, '2024-12-10', '超速罚款', 1),
(5, 'OTHER', '拖车费', 1100.00, '2024-12-10', '事故拖车费用', 1)
ON DUPLICATE KEY UPDATE `cost_name` = VALUES(`cost_name`);

-- 插入示例订单状态日志数据
INSERT INTO `order_status_log` (`order_id`, `status_type`, `old_status`, `new_status`, `change_reason`, `operator_id`, `operator_name`) VALUES
(1, 'TRANSPORT', NULL, 'CREATED', '创建订单', 1, '管理员'),
(1, 'PAYMENT', NULL, 'UNPAID', '创建订单', 1, '管理员'),
(1, 'TRANSPORT', 'CREATED', 'DEPARTED', '司机已出发', 1, '管理员'),
(1, 'TRANSPORT', 'DEPARTED', 'TRANSPORTING', '开始运输', 1, '管理员'),
(1, 'TRANSPORT', 'TRANSPORTING', 'DELIVERED', '货物已送达', 1, '管理员'),
(1, 'PAYMENT', 'UNPAID', 'PAID', '客户已付款', 1, '管理员'),

(2, 'TRANSPORT', NULL, 'CREATED', '创建订单', 1, '管理员'),
(2, 'PAYMENT', NULL, 'UNPAID', '创建订单', 1, '管理员'),
(2, 'PAYMENT', 'UNPAID', 'PREPAID', '预付部分款项', 1, '管理员'),
(2, 'TRANSPORT', 'CREATED', 'DEPARTED', '司机已出发', 1, '管理员'),
(2, 'TRANSPORT', 'DEPARTED', 'TRANSPORTING', '开始运输', 1, '管理员'),

(3, 'TRANSPORT', NULL, 'CREATED', '创建订单', 1, '管理员'),
(3, 'PAYMENT', NULL, 'UNPAID', '创建订单', 1, '管理员'),

(4, 'TRANSPORT', NULL, 'CREATED', '创建订单', 1, '管理员'),
(4, 'PAYMENT', NULL, 'UNPAID', '创建订单', 1, '管理员'),
(4, 'TRANSPORT', 'CREATED', 'DEPARTED', '司机已出发', 1, '管理员'),

(5, 'TRANSPORT', NULL, 'CREATED', '创建订单', 1, '管理员'),
(5, 'PAYMENT', NULL, 'UNPAID', '创建订单', 1, '管理员'),
(5, 'PAYMENT', 'UNPAID', 'PREPAID', '预付部分款项', 1, '管理员'),
(5, 'TRANSPORT', 'CREATED', 'DEPARTED', '司机已出发', 1, '管理员'),
(5, 'TRANSPORT', 'DEPARTED', 'TRANSPORTING', '开始运输', 1, '管理员'),
(5, 'TRANSPORT', 'TRANSPORTING', 'EXCEPTION', '路上遇到交通事故', 1, '管理员')
ON DUPLICATE KEY UPDATE `change_reason` = VALUES(`change_reason`);
