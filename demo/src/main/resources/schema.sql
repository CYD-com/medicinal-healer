-- 创建数据库
CREATE DATABASE IF NOT EXISTS user_generator DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE user_generator;

-- 用户表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码（加密后）',
    `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    `gender` VARCHAR(10) DEFAULT NULL COMMENT '性别：male-男，female-女，other-其他',
    `age` INT DEFAULT NULL COMMENT '年龄',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `id_card` VARCHAR(20) DEFAULT NULL COMMENT '身份证号',
    `address` VARCHAR(200) DEFAULT NULL COMMENT '地址',
    `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    `role` VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色：user-用户，admin-管理员',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_role` (`role`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_phone` (`phone`),
    KEY `idx_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 记录表
DROP TABLE IF EXISTS `t_record`;
CREATE TABLE `t_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `category` VARCHAR(50) NOT NULL COMMENT '分类',
    `type` VARCHAR(20) NOT NULL COMMENT '类型：收入/支出',
    `status` VARCHAR(20) NOT NULL COMMENT '状态：已完成/待支付/已取消',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '金额',
    `time` DATE NOT NULL COMMENT '时间',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category`),
    KEY `idx_type` (`type`),
    KEY `idx_status` (`status`),
    KEY `idx_time` (`time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='记录表';

-- 插入测试数据
INSERT INTO `sys_user` (`username`, `password`, `role`, `create_time`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'admin', NOW()),
('user1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'user', NOW());

-- 插入测试记录数据
INSERT INTO `t_record` (`category`, `type`, `status`, `amount`, `time`) VALUES
('餐饮', '支出', '已完成', 100.00, '2024-01-01'),
('交通', '支出', '已完成', 50.00, '2024-01-02'),
('购物', '支出', '待支付', 200.00, '2024-01-03'),
('娱乐', '支出', '已完成', 150.00, '2024-01-04'),
('医疗', '支出', '已取消', 300.00, '2024-01-05'),
('餐饮', '支出', '已完成', 80.00, '2024-01-06'),
('交通', '支出', '已完成', 45.00, '2024-01-07'),
('购物', '支出', '已完成', 120.00, '2024-01-08'),
('娱乐', '支出', '待支付', 180.00, '2024-01-09'),
('餐饮', '支出', '已完成', 90.00, '2024-01-10');
