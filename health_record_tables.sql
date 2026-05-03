CREATE TABLE IF NOT EXISTS `health_record` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `height` INT DEFAULT NULL COMMENT '身高(cm)',
    `weight` INT DEFAULT NULL COMMENT '体重(kg)',
    `blood_type` VARCHAR(10) DEFAULT NULL COMMENT '血型',
    `marital_status` VARCHAR(20) DEFAULT NULL COMMENT '婚姻状况',
    `health_score` INT DEFAULT 85 COMMENT '健康评分',
    `total_visits` INT DEFAULT 0 COMMENT '总就诊次数',
    `total_prescriptions` INT DEFAULT 0 COMMENT '处方数量',
    `last_visit_date` VARCHAR(20) DEFAULT NULL COMMENT '最近就诊日期',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康档案表';

CREATE TABLE IF NOT EXISTS `past_disease` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `disease_name` VARCHAR(100) NOT NULL COMMENT '疾病名称',
    `diagnosis_date` VARCHAR(20) DEFAULT NULL COMMENT '诊断日期',
    `diagnosis_hospital` VARCHAR(200) DEFAULT NULL COMMENT '诊断医院',
    `current_status` VARCHAR(50) DEFAULT NULL COMMENT '当前状态',
    `treatment` VARCHAR(200) DEFAULT NULL COMMENT '治疗方式',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='既往病史表';

CREATE TABLE IF NOT EXISTS `family_disease` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `disease_name` VARCHAR(100) NOT NULL COMMENT '疾病名称',
    `relation` VARCHAR(50) DEFAULT NULL COMMENT '关系',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='家族病史表';


// 过敏史表
CREATE TABLE IF NOT EXISTS `allergy` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `allergen` VARCHAR(100) NOT NULL COMMENT '过敏原',
    `reaction` VARCHAR(200) DEFAULT NULL COMMENT '反应',
    `severity` VARCHAR(50) DEFAULT NULL COMMENT '严重程度',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='过敏史表';

// 手术史表
CREATE TABLE IF NOT EXISTS `surgical_history` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `surgery_name` VARCHAR(200) NOT NULL COMMENT '手术名称',
    `surgery_date` VARCHAR(20) DEFAULT NULL COMMENT '手术日期',
    `hospital` VARCHAR(200) DEFAULT NULL COMMENT '医院',
    `recovery` VARCHAR(100) DEFAULT NULL COMMENT '恢复情况',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='手术史表';

CREATE TABLE IF NOT EXISTS `health_indicator` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `indicator_type` VARCHAR(50) NOT NULL COMMENT '指标类型(bloodPressure/bloodSugar/weight/height)',
    `systolic` INT DEFAULT NULL COMMENT '收缩压',
    `diastolic` INT DEFAULT NULL COMMENT '舒张压',
    `heart_rate` INT DEFAULT NULL COMMENT '心率',
    `record_date` VARCHAR(20) NOT NULL COMMENT '记录日期',
    `record_time` VARCHAR(20) DEFAULT NULL COMMENT '记录时间',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `source` VARCHAR(50) DEFAULT 'manual' COMMENT '来源(manual/device)',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_indicator_type` (`indicator_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康指标记录表';

// 就诊记录表
CREATE TABLE IF NOT EXISTS `visit_record` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `visit_type` VARCHAR(50) DEFAULT 'outpatient' COMMENT '就诊类型(outpatient/inpatient)',
    `visit_date` VARCHAR(20) NOT NULL COMMENT '就诊日期',
    `department` VARCHAR(100) DEFAULT NULL COMMENT '科室',
    `doctor_id` BIGINT DEFAULT NULL COMMENT '医生ID',
    `chief_complaint` VARCHAR(500) DEFAULT NULL COMMENT '主诉',
    `diagnosis` VARCHAR(500) DEFAULT NULL COMMENT '诊断',
    `treatment` VARCHAR(500) DEFAULT NULL COMMENT '治疗方案',
    `prescription_id` BIGINT DEFAULT NULL COMMENT '处方ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_doctor_id` (`doctor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='就诊记录表';

CREATE TABLE IF NOT EXISTS `health_record_authorization` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `target_type` VARCHAR(50) NOT NULL COMMENT '授权对象类型(doctor/family)',
    `target_id` BIGINT NOT NULL COMMENT '授权对象ID',
    `permissions` TEXT COMMENT '权限列表(JSON)',
    `expires_at` DATETIME DEFAULT NULL COMMENT '过期时间',
    `status` VARCHAR(50) DEFAULT 'active' COMMENT '状态(active/inactive)',
    `authorized_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '授权时间',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_target` (`target_type`, `target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康档案授权表';

INSERT INTO `health_record` (`user_id`, `height`, `weight`, `blood_type`, `marital_status`, `health_score`, `total_visits`, `total_prescriptions`, `last_visit_date`) VALUES
(1, 175, 70, 'A', 'married', 85, 15, 23, '2026-03-15');

INSERT INTO `past_disease` (`user_id`, `disease_name`, `diagnosis_date`, `diagnosis_hospital`, `current_status`, `treatment`, `remark`) VALUES
(1, '高血压', '2020-05-01', '朝阳医院', '控制中', '药物治疗', '每日服药');

INSERT INTO `family_disease` (`user_id`, `disease_name`, `relation`, `remark`) VALUES
(1, '糖尿病', '父亲', '2型糖尿病');

INSERT INTO `allergy` (`user_id`, `allergen`, `reaction`, `severity`) VALUES
(1, '青霉素', '皮疹', '中等');

INSERT INTO `surgical_history` (`user_id`, `surgery_name`, `surgery_date`, `hospital`, `recovery`) VALUES
(1, '阑尾切除术', '2015-03-10', '协和医院', '良好');

INSERT INTO `health_indicator` (`user_id`, `indicator_type`, `systolic`, `diastolic`, `heart_rate`, `record_date`, `record_time`, `remark`, `source`) VALUES
(1, 'bloodPressure', 135, 85, 72, '2026-04-08', '08:30', '晨起测量', 'manual'),
(1, 'bloodPressure', 138, 88, 70, '2026-04-07', '08:15', '晨起测量', 'manual'),
(1, 'bloodPressure', 132, 82, 74, '2026-04-06', '08:20', '晨起测量', 'manual'),
(1, 'bloodPressure', 140, 90, 76, '2026-04-05', '08:25', '晨起测量', 'manual'),
(1, 'bloodPressure', 136, 86, 72, '2026-04-04', '08:30', '晨起测量', 'manual');