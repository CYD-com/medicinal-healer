-- 处方管理建表语句

-- 1. 处方主表 (t_prescription)
CREATE TABLE IF NOT EXISTS `t_prescription` (
    `prescription_id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '处方ID',
    `prescription_no` VARCHAR(50) NOT NULL UNIQUE COMMENT '处方编号',
    `user_id` BIGINT NOT NULL COMMENT '患者用户ID',
    `doctor_id` BIGINT NOT NULL COMMENT '医生ID',
    `diagnosis` VARCHAR(500) NOT NULL COMMENT '诊断',
    `doctor_advice` TEXT COMMENT '医嘱',
    `total_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '总金额',
    `drug_count` INT NOT NULL DEFAULT 0 COMMENT '药品数量',
    `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态：pending-待审核，approved-已审核，rejected-已拒绝，completed-已完成，expired-已过期',
    `valid_until` DATETIME COMMENT '有效期至',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
    INDEX `idx_prescription_user` (`user_id`),
    INDEX `idx_prescription_doctor` (`doctor_id`),
    INDEX `idx_prescription_status` (`status`),
    INDEX `idx_prescription_no` (`prescription_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='处方表';

-- 2. 处方药品明细表 (t_prescription_item)
CREATE TABLE IF NOT EXISTS `t_prescription_item` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `prescription_id` BIGINT NOT NULL COMMENT '处方ID',
    `drug_id` VARCHAR(50) NOT NULL COMMENT '药品ID',
    `drug_name` VARCHAR(100) NOT NULL COMMENT '药品名称',
    `specification` VARCHAR(100) COMMENT '规格',
    `dosage` VARCHAR(200) COMMENT '用法用量',
    `quantity` INT NOT NULL DEFAULT 1 COMMENT '数量',
    `unit` VARCHAR(20) NOT NULL DEFAULT '盒' COMMENT '单位',
    `unit_price` DECIMAL(10,2) NOT NULL COMMENT '单价',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '金额',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_item_prescription` (`prescription_id`),
    INDEX `idx_item_drug` (`drug_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='处方药品明细表';
