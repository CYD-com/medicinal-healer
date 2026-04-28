CREATE TABLE IF NOT EXISTS `t_department` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '科室ID',
  `name` VARCHAR(100) NOT NULL COMMENT '科室名称',
  `description` VARCHAR(500) COMMENT '科室描述',
  `doctors_count` INT DEFAULT 0 COMMENT '医生数量',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_department_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='科室表';

CREATE TABLE IF NOT EXISTS `t_doctor` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '医生ID',
  `name` VARCHAR(100) NOT NULL COMMENT '医生姓名',
  `title` VARCHAR(50) COMMENT '职称',
  `avatar` VARCHAR(500) COMMENT '头像URL',
  `rating` DECIMAL(3,1) DEFAULT 0 COMMENT '评分',
  `consultation_count` INT DEFAULT 0 COMMENT '问诊量',
  `specialty` VARCHAR(500) COMMENT '擅长领域（用顿号分隔）',
  `department_id` BIGINT COMMENT '所属科室ID',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_doctor_name` (`name`),
  INDEX `idx_doctor_department` (`department_id`),
  FOREIGN KEY (`department_id`) REFERENCES `t_department`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医生表';

CREATE TABLE IF NOT EXISTS `t_appointment` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '预约ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `doctor_id` BIGINT NOT NULL COMMENT '医生ID',
  `department_id` BIGINT NOT NULL COMMENT '科室ID',
  `appointment_date` DATE NOT NULL COMMENT '预约日期',
  `start_time` VARCHAR(20) NOT NULL COMMENT '开始时间',
  `end_time` VARCHAR(20) NOT NULL COMMENT '结束时间',
  `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态：pending-待确认，confirmed-已确认，completed-已完成，cancelled-已取消',
  `patient_name` VARCHAR(100) NOT NULL COMMENT '患者姓名',
  `patient_phone` VARCHAR(20) NOT NULL COMMENT '患者电话',
  `symptoms` VARCHAR(1000) COMMENT '症状描述',
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_appointment_user` (`user_id`),
  INDEX `idx_appointment_doctor` (`doctor_id`),
  INDEX `idx_appointment_date` (`appointment_date`),
  INDEX `idx_appointment_status` (`status`),
  FOREIGN KEY (`user_id`) REFERENCES `sys_user`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`doctor_id`) REFERENCES `t_doctor`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`department_id`) REFERENCES `t_department`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预约表';

INSERT INTO `t_department` (`name`, `description`, `doctors_count`) VALUES
('内科', '内科诊疗', 2),
('外科', '外科诊疗', 1),
('儿科', '儿科诊疗', 1),
('妇产科', '妇产科诊疗', 1);

INSERT INTO `t_doctor` (`name`, `title`, `avatar`, `rating`, `consultation_count`, `specialty`, `department_id`) VALUES
('张医生', '主任医师', '', 4.9, 1200, '心血管、高血压', 1),
('李医生', '副主任医师', '', 4.8, 800, '糖尿病、内分泌', 1),
('王医生', '主任医师', '', 4.7, 1500, '骨科、关节', 2),
('赵医生', '主治医师', '', 4.9, 600, '儿科呼吸、发育', 3),
('刘医生', '主任医师', '', 4.8, 2000, '妇产科、产前检查', 4);