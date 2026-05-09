-- ============================================
-- 社区医疗服务系统 - 完整建表 SQL
-- 共 18 张表，每表插入 5 条测试数据
-- 执行顺序：按依赖关系排列
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS liangxi DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE liangxi;

-- ============================================
-- 1. sys_user 用户表（基础表，无外键依赖）
-- ============================================
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    `gender` VARCHAR(10) DEFAULT NULL COMMENT '性别',
    `age` INT DEFAULT NULL COMMENT '年龄',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '电话',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `id_card` VARCHAR(18) DEFAULT NULL COMMENT '身份证号',
    `address` VARCHAR(255) DEFAULT NULL COMMENT '地址',
    `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    `role` VARCHAR(20) DEFAULT 'user' COMMENT '角色：user-用户，admin-管理员，doctor-医生',
    `status` INT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_phone` (`phone`),
    UNIQUE KEY `uk_email` (`email`),
    UNIQUE KEY `uk_id_card` (`id_card`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 测试密码均为 123456（BCrypt加密）
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `gender`, `age`, `phone`, `email`, `id_card`, `address`, `role`, `status`, `create_time`) VALUES
('admin',    '$2b$10$uRAS68xyAueDj1s/o0UZjO.k4JOW1.QWWwajImr/d1RiFx2z9l47y', '系统管理员', '男', 35, '13800000001', 'admin@community.com',    '110101198901010001', '北京市东城区社区卫生服务中心',   'admin',  1, NOW()),
('doctor_zs','$2b$10$uRAS68xyAueDj1s/o0UZjO.k4JOW1.QWWwajImr/d1RiFx2z9l47y', '张三',       '男', 45, '13800000002', 'zhangsan@community.com', '110101197901010002', '北京市朝阳区阳光社区',           'doctor', 1, NOW()),
('doctor_ls','$2b$10$uRAS68xyAueDj1s/o0UZjO.k4JOW1.QWWwajImr/d1RiFx2z9l47y', '李四',       '女', 38, '13800000003', 'lisi@community.com',     '110101198601010003', '北京市海淀区中关村社区',         'doctor', 1, NOW()),
('user_w5',  '$2b$10$uRAS68xyAueDj1s/o0UZjO.k4JOW1.QWWwajImr/d1RiFx2z9l47y', '王五',       '男', 28, '13800000004', 'wangwu@community.com',   '110101199601010004', '北京市西城区新街口社区',         'user',   1, NOW()),
('user_zl',  '$2b$10$uRAS68xyAueDj1s/o0UZjO.k4JOW1.QWWwajImr/d1RiFx2z9l47y', '赵六',       '女', 55, '13800000005', 'zhaoliu@community.com',  '110101196901010005', '北京市丰台区方庄社区',           'user',   1, NOW()),
('doctor_wf', '$2b$10$uRAS68xyAueDj1s/o0UZjO.k4JOW1.QWWwajImr/d1RiFx2z9l47y', '王芳', '女', 38, '13800000006', 'wangfang@hospital.com', '110101198601010006', '北京市海淀区中关村社区', 'doctor', 1, NOW()),
('doctor_cq', '$2b$10$uRAS68xyAueDj1s/o0UZjO.k4JOW1.QWWwajImr/d1RiFx2z9l47y', '陈强', '男', 50, '13800000007', 'chenqiang@hospital.com', '110101197401010007', '北京市朝阳区望京社区', 'doctor', 1, NOW()),
('doctor_lm', '$2b$10$uRAS68xyAueDj1s/o0UZjO.k4JOW1.QWWwajImr/d1RiFx2z9l47y', '刘梅', '女', 45, '13800000008', 'liumei@hospital.com', '110101197901010008', '北京市东城区东直门社区', 'doctor', 1, NOW());

-- ============================================
-- 2. t_department 科室表（无外键依赖）
-- ============================================
DROP TABLE IF EXISTS `t_department`;
CREATE TABLE `t_department` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '科室ID',
    `name` VARCHAR(100) NOT NULL COMMENT '科室名称',
    `description` TEXT COMMENT '科室描述',
    `doctors_count` INT DEFAULT 0 COMMENT '医生数量',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科室表';

INSERT INTO `t_department` (`name`, `description`, `doctors_count`, `create_time`) VALUES
('全科门诊',  '提供常见病、多发病的诊疗服务，是社区医疗的核心科室',           3, NOW()),
('内科',      '专注于心血管、呼吸系统、消化系统等内科疾病的诊治',             2, NOW()),
('外科',      '提供常见外科疾病的诊治和小型手术服务',                         1, NOW()),
('妇产科',    '提供妇科疾病诊疗、产前检查、产后康复等服务',                   1, NOW()),
('中医科',    '提供中医诊疗、针灸推拿、中药调理等传统医学服务',               2, NOW());

-- ============================================
-- 3. drug_categories 药品分类表（无外键依赖）
-- ============================================
DROP TABLE IF EXISTS `drug_categories`;
CREATE TABLE `drug_categories` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name` VARCHAR(100) NOT NULL COMMENT '分类名称',
    `description` TEXT COMMENT '分类描述',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='药品分类表';

INSERT INTO `drug_categories` (`name`, `description`, `created_at`) VALUES
('西药',      '化学合成药物，包括片剂、胶囊、注射剂等',           NOW()),
('中成药',    '以中药材为原料，按规定的处方和工艺制成的药品',       NOW()),
('中药饮片',  '中药材经过炮制后可直接用于中医临床的药品',           NOW()),
('生物制品',  '利用生物技术制备的药物，如疫苗、血液制品等',         NOW()),
('医疗器械',  '用于医疗诊断、治疗、康复的器械和设备',               NOW());

-- ============================================
-- 4. drugs 药品表（无外键依赖）
-- ============================================
DROP TABLE IF EXISTS `drugs`;
CREATE TABLE `drugs` (
    `drug_id` VARCHAR(36) NOT NULL COMMENT '药品ID',
    `drug_name` VARCHAR(100) NOT NULL COMMENT '药品名称',
    `generic_name` VARCHAR(100) DEFAULT NULL COMMENT '通用名',
    `specification` VARCHAR(100) DEFAULT NULL COMMENT '规格',
    `manufacturer` VARCHAR(200) DEFAULT NULL COMMENT '生产厂家',
    `category` VARCHAR(50) DEFAULT NULL COMMENT '分类',
    `type` VARCHAR(20) DEFAULT NULL COMMENT '类型：prescription-处方药，otc-非处方药',
    `price` DECIMAL(10,2) DEFAULT NULL COMMENT '价格',
    `stock` INT DEFAULT 0 COMMENT '库存数量',
    `unit` VARCHAR(20) DEFAULT NULL COMMENT '单位',
    `english_name` VARCHAR(200) DEFAULT NULL COMMENT '英文名',
    `approval_no` VARCHAR(100) DEFAULT NULL COMMENT '批准文号',
    `form` VARCHAR(50) DEFAULT NULL COMMENT '剂型',
    `shelf_life` VARCHAR(50) DEFAULT NULL COMMENT '有效期',
    `storage` VARCHAR(100) DEFAULT NULL COMMENT '贮藏条件',
    `indications` TEXT COMMENT '适应症',
    `contraindications` TEXT COMMENT '禁忌',
    `adverse_reactions` TEXT COMMENT '不良反应',
    `precautions` TEXT COMMENT '注意事项',
    `drug_interactions` TEXT COMMENT '药物相互作用',
    `dosage` VARCHAR(200) DEFAULT NULL COMMENT '用法用量',
    `image` VARCHAR(500) DEFAULT NULL COMMENT '药品图片',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`drug_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='药品表';

INSERT INTO `drugs` (`drug_id`, `drug_name`, `generic_name`, `specification`, `manufacturer`, `category`, `type`, `price`, `stock`, `unit`, `approval_no`, `form`, `shelf_life`, `indications`, `dosage`, `created_at`) VALUES
('DRUG001', '阿莫西林胶囊',   '阿莫西林',   '0.5g*24粒', '华北制药股份有限公司',   '西药', 'prescription', 15.80,  500, '盒', '国药准字H13023964', '胶囊剂', '24个月', '敏感菌所致的感染：呼吸道感染、泌尿生殖道感染、皮肤软组织感染', '口服，一次0.5g，每6-8小时一次', NOW()),
('DRUG002', '布洛芬缓释胶囊', '布洛芬',     '0.3g*20粒', '中美天津史克制药有限公司', '西药', 'otc',          22.50,  300, '盒', '国药准字H10900089', '胶囊剂', '36个月', '用于缓解轻至中度疼痛如头痛、关节痛、偏头痛、牙痛、肌肉痛、神经痛、痛经', '口服，一次0.3g，一日2次', NOW()),
('DRUG003', '复方丹参滴丸',   '复方丹参滴丸', '27mg*180丸', '天士力医药集团股份有限公司', '中成药', 'prescription', 38.00,  200, '盒', '国药准字Z10950111', '滴丸剂', '36个月', '活血化瘀，理气止痛。用于气滞血瘀所致的胸痹，症见胸闷、心前区刺痛', '口服或舌下含服，一次10丸，一日3次', NOW()),
('DRUG004', '感冒灵颗粒',     '感冒灵颗粒', '10g*9袋',   '华润三九医药股份有限公司', '中成药', 'otc',          16.50,  400, '盒', '国药准字Z44021940', '颗粒剂', '24个月', '解热镇痛。用于感冒引起的头痛，发热，鼻塞，流涕，咽痛', '开水冲服，一次10g，一日3次', NOW()),
('DRUG005', '盐酸二甲双胍片', '二甲双胍',   '0.25g*48片', '中美上海施贵宝制药有限公司', '西药', 'prescription', 12.80,  600, '盒', '国药准字H20023370', '片剂', '36个月', '用于2型糖尿病，特别是肥胖的2型糖尿病患者', '口服，开始一次0.25g，一日2-3次，以后根据疗效逐渐加量', NOW());

-- ============================================
-- 5. t_doctor 医生表（依赖 t_department, sys_user）
-- ============================================
DROP TABLE IF EXISTS `t_doctor`;
CREATE TABLE `t_doctor` (
    `doctor_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '医生ID',
    `title` VARCHAR(50) DEFAULT NULL COMMENT '职称',
    `rating` DECIMAL(2,1) DEFAULT 5.0 COMMENT '评分',
    `consultation_count` INT DEFAULT 0 COMMENT '问诊次数',
    `specialty` VARCHAR(500) DEFAULT NULL COMMENT '擅长方向',
    `department_id` BIGINT DEFAULT NULL COMMENT '科室ID',
    `user_id` BIGINT DEFAULT NULL COMMENT '关联用户ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`doctor_id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    KEY `idx_department_id` (`department_id`),
    CONSTRAINT `fk_doctor_department` FOREIGN KEY (`department_id`) REFERENCES `t_department`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医生表';

INSERT INTO `t_doctor` (`title`, `rating`, `consultation_count`, `specialty`, `department_id`, `user_id`, `create_time`) VALUES
('主任医师',   4.9, 1286, '心血管疾病、高血压、冠心病',       2, 2, NOW()),
('副主任医师', 4.8, 956,  '呼吸系统疾病、慢性支气管炎、哮喘', 2, 3, NOW()),
('主治医师',   4.7, 623,  '常见病、多发病、慢性病管理',       1, 7, NOW()),
('主任医师',   4.9, 1102, '骨科疾病、关节炎、腰椎间盘突出',   3, 8, NOW()),
('副主任医师', 4.8, 845,  '中医内科、针灸推拿、亚健康调理',   5, 9, NOW());

-- ============================================
-- 6. t_appointment 预约挂号表（依赖 sys_user, t_doctor, t_department）
-- ============================================
DROP TABLE IF EXISTS `t_appointment`;
CREATE TABLE `t_appointment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '预约ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `doctor_id` BIGINT NOT NULL COMMENT '医生ID',
    `department_id` BIGINT NOT NULL COMMENT '科室ID',
    `appointment_date` DATE NOT NULL COMMENT '预约日期',
    `start_time` TIME NOT NULL COMMENT '开始时间',
    `end_time` TIME NOT NULL COMMENT '结束时间',
    `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态：pending-待确认，confirmed-已确认，completed-已完成，cancelled-已取消',
    `patient_name` VARCHAR(50) DEFAULT NULL COMMENT '患者姓名',
    `patient_phone` VARCHAR(20) DEFAULT NULL COMMENT '患者电话',
    `symptoms` TEXT COMMENT '症状描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_doctor_id` (`doctor_id`),
    KEY `idx_department_id` (`department_id`),
    KEY `idx_appointment_date` (`appointment_date`),
    CONSTRAINT `fk_appointment_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user`(`id`),
    CONSTRAINT `fk_appointment_doctor` FOREIGN KEY (`doctor_id`) REFERENCES `t_doctor`(`doctor_id`),
    CONSTRAINT `fk_appointment_department` FOREIGN KEY (`department_id`) REFERENCES `t_department`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约挂号表';

INSERT INTO `t_appointment` (`user_id`, `doctor_id`, `department_id`, `appointment_date`, `start_time`, `end_time`, `status`, `patient_name`, `patient_phone`, `symptoms`, `create_time`) VALUES
(4, 1, 2, DATE_ADD(CURDATE(), INTERVAL 0 DAY),  '09:00', '09:30', 'confirmed',  '王五', '13800000004', '头晕、胸闷，持续三天，伴有轻微心悸',         NOW()),
(5, 2, 2, DATE_ADD(CURDATE(), INTERVAL 0 DAY),  '09:30', '10:00', 'pending',    '赵六', '13800000005', '咳嗽、咳痰一周，夜间加重，无发热',           NOW()),
(4, 3, 1, DATE_ADD(CURDATE(), INTERVAL 0 DAY),  '10:00', '10:30', 'pending',    '王五', '13800000004', '近两周感觉乏力、食欲不振，偶有低热',         NOW()),
(5, 1, 2, DATE_ADD(CURDATE(), INTERVAL -1 DAY), '14:00', '14:30', 'completed',  '赵六', '13800000005', '高血压复查，近期血压波动较大，伴有头痛',     NOW()),
(4, 5, 5, DATE_ADD(CURDATE(), INTERVAL 1 DAY),  '08:30', '09:00', 'pending',    '王五', '13800000004', '失眠多梦两周，工作压力大，腰膝酸软',         NOW()),
(5, 1, 2, DATE_ADD(CURDATE(), INTERVAL 1 DAY),  '10:00', '10:30', 'pending',    '赵六', '13800000005', '反复感冒两周，鼻塞流涕，咽痛',               NOW()),
(4, 4, 3, DATE_ADD(CURDATE(), INTERVAL 1 DAY),  '08:30', '09:00', 'pending',    '王五', '13800000004', '腰痛一周，弯腰时加重，活动受限',             NOW());

-- ============================================
-- 7. consultation 在线问诊表（依赖 sys_user, t_doctor）
-- ============================================
DROP TABLE IF EXISTS `consultation`;
CREATE TABLE `consultation` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `consultation_no` VARCHAR(50) NOT NULL UNIQUE COMMENT '问诊编号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `doctor_id` BIGINT NOT NULL COMMENT '医生ID',
    `consultation_type` VARCHAR(20) NOT NULL COMMENT '问诊类型：text-图文，video-视频',
    `symptom` VARCHAR(200) NOT NULL COMMENT '主诉',
    `description` TEXT NOT NULL COMMENT '详细描述',
    `images` TEXT COMMENT '图片URL，逗号分隔',
    `medical_history` TEXT COMMENT '既往病史',
    `current_medication` TEXT COMMENT '当前用药',
    `doctor_reply` TEXT COMMENT '医生回复',
    `diagnosis` TEXT COMMENT '诊断结果',
    `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态：pending-待回复，in_progress-进行中，completed-已完成，closed-已关闭',
    `created_at` DATETIME NOT NULL COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_doctor_id` (`doctor_id`),
    INDEX `idx_status` (`status`),
    FOREIGN KEY (`user_id`) REFERENCES `sys_user`(`id`),
    FOREIGN KEY (`doctor_id`) REFERENCES `t_doctor`(`doctor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='在线问诊表';

INSERT INTO `consultation` (`consultation_no`, `user_id`, `doctor_id`, `consultation_type`, `symptom`, `description`, `medical_history`, `current_medication`, `doctor_reply`, `diagnosis`, `status`, `created_at`, `updated_at`) VALUES
('CON20260501001', 4, 1, 'text', '胸闷气短',     '最近一周经常感到胸闷，爬楼梯时气短明显，偶有心悸',                   '高血压病史3年',         '硝苯地平缓释片',     '建议做心电图和心脏彩超检查，目前血压控制情况需要评估', '疑似冠心病早期表现，需进一步检查确认', 'completed',   NOW(), NOW()),
('CON20260502001', 5, 2, 'text', '持续咳嗽',     '咳嗽两周未愈，有白色粘痰，夜间加重，无发热，无盗汗',                 '慢性支气管炎病史',      '无',                 '建议拍胸片排除肺部感染，可先服用止咳化痰药物观察',         '慢性支气管炎急性发作',                  'in_progress', NOW(), NOW()),
('CON20260503001', 4, 3, 'text', '皮肤瘙痒',     '双臂出现红色皮疹，瘙痒明显，持续三天，未接触新食物或物品',           '无特殊病史',            '无',                 NULL, NULL, 'pending',   NOW(), NOW()),
('CON20260503002', 5, 1, 'video', '血压波动',     '近两周血压不稳定，早晨偏高（150/95），伴有头晕，调整用药后无改善',   '高血压5年，糖尿病2年',  '硝苯地平、二甲双胍', '视频问诊已安排，届时会详细评估您的血压控制方案',           '高血压合并糖尿病，需优化用药方案',      'in_progress', NOW(), NOW()),
('CON20260504001', 4, 5, 'text', '失眠乏力',     '近三周入睡困难，多梦易醒，白天精神差，食欲下降，腰膝酸软',           '无特殊病史',            '无',                 NULL, NULL, 'pending',   NOW(), NOW());

-- ============================================
-- 8. t_prescription 处方表（依赖 sys_user, t_doctor）
-- ============================================
DROP TABLE IF EXISTS `t_prescription`;
CREATE TABLE `t_prescription` (
    `prescription_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '处方ID',
    `prescription_no` VARCHAR(50) NOT NULL COMMENT '处方编号',
    `user_id` BIGINT NOT NULL COMMENT '患者用户ID',
    `doctor_id` BIGINT NOT NULL COMMENT '医生ID',
    `diagnosis` TEXT COMMENT '诊断结果',
    `doctor_advice` TEXT COMMENT '医嘱',
    `total_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '总金额',
    `drug_count` INT DEFAULT 0 COMMENT '药品数量',
    `status` VARCHAR(20) DEFAULT 'valid' COMMENT '状态：valid-有效，expired-已过期，used-已使用',
    `valid_until` DATETIME COMMENT '有效期至',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
    PRIMARY KEY (`prescription_id`),
    UNIQUE KEY `uk_prescription_no` (`prescription_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_doctor_id` (`doctor_id`),
    CONSTRAINT `fk_prescription_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user`(`id`),
    CONSTRAINT `fk_prescription_doctor` FOREIGN KEY (`doctor_id`) REFERENCES `t_doctor`(`doctor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='处方表';

INSERT INTO `t_prescription` (`prescription_no`, `user_id`, `doctor_id`, `diagnosis`, `doctor_advice`, `total_amount`, `drug_count`, `status`, `valid_until`, `created_at`) VALUES
('RX20260501001', 4, 1, '高血压2级，冠心病待排',       '低盐低脂饮食，适量运动，避免情绪激动，定期监测血压', 76.60,  2, 'valid',   '2026-06-04 00:00:00', NOW()),
('RX20260502001', 5, 2, '慢性支气管炎急性发作',         '注意保暖，避免受凉，多饮温水，忌辛辣刺激食物',       54.30,  2, 'valid',   '2026-06-02 00:00:00', NOW()),
('RX20260503001', 4, 3, '上呼吸道感染',                 '多休息，多饮水，清淡饮食，按时服药',                 32.30,  2, 'valid',   '2026-06-03 00:00:00', NOW()),
('RX20260504001', 5, 1, '高血压合并2型糖尿病',           '严格控制饮食，规律运动，每日监测血压和血糖',         50.80,  2, 'valid',   '2026-06-04 00:00:00', NOW()),
('RX20260401001', 4, 1, '高血压1级',                     '低盐饮食，适量运动',                                 38.00,  1, 'expired', '2026-05-01 00:00:00', '2026-04-01 10:00:00');

-- ============================================
-- 9. t_prescription_item 处方明细表（依赖 t_prescription, drugs）
-- ============================================
DROP TABLE IF EXISTS `t_prescription_item`;
CREATE TABLE `t_prescription_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '明细ID',
    `prescription_id` BIGINT NOT NULL COMMENT '处方ID',
    `drug_id` VARCHAR(36) NOT NULL COMMENT '药品ID',
    `drug_name` VARCHAR(100) NOT NULL COMMENT '药品名称',
    `specification` VARCHAR(100) DEFAULT NULL COMMENT '规格',
    `dosage` VARCHAR(100) DEFAULT NULL COMMENT '用法用量',
    `quantity` INT NOT NULL DEFAULT 1 COMMENT '数量',
    `unit` VARCHAR(20) DEFAULT NULL COMMENT '单位',
    `unit_price` DECIMAL(10,2) DEFAULT NULL COMMENT '单价',
    `amount` DECIMAL(10,2) DEFAULT NULL COMMENT '金额',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_prescription_id` (`prescription_id`),
    CONSTRAINT `fk_item_prescription` FOREIGN KEY (`prescription_id`) REFERENCES `t_prescription`(`prescription_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='处方明细表';

INSERT INTO `t_prescription_item` (`prescription_id`, `drug_id`, `drug_name`, `specification`, `dosage`, `quantity`, `unit`, `unit_price`, `amount`, `created_at`) VALUES
(1, 'DRUG005', '盐酸二甲双胍片', '0.25g*48片', '口服，一次0.25g，一日2-3次', 1, '盒', 12.80, 12.80, NOW()),
(1, 'DRUG003', '复方丹参滴丸',   '27mg*180丸', '口服，一次10丸，一日3次',     1, '盒', 38.00, 38.00, NOW()),
(2, 'DRUG001', '阿莫西林胶囊',   '0.5g*24粒',  '口服，一次0.5g，每8小时一次',  2, '盒', 15.80, 31.60, NOW()),
(2, 'DRUG004', '感冒灵颗粒',     '10g*9袋',    '开水冲服，一次10g，一日3次',   1, '盒', 16.50, 16.50, NOW()),
(3, 'DRUG002', '布洛芬缓释胶囊', '0.3g*20粒',  '口服，一次0.3g，一日2次',      1, '盒', 22.50, 22.50, NOW());

-- ============================================
-- 10. health_record 健康档案表（依赖 sys_user）
-- ============================================
DROP TABLE IF EXISTS `health_record`;
CREATE TABLE `health_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '档案ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `height` DECIMAL(5,1) DEFAULT NULL COMMENT '身高(cm)',
    `weight` DECIMAL(5,1) DEFAULT NULL COMMENT '体重(kg)',
    `blood_type` VARCHAR(10) DEFAULT NULL COMMENT '血型',
    `marital_status` VARCHAR(20) DEFAULT NULL COMMENT '婚姻状况',
    `health_score` INT DEFAULT 0 COMMENT '健康评分',
    `total_visits` INT DEFAULT 0 COMMENT '总就诊次数',
    `total_prescriptions` INT DEFAULT 0 COMMENT '总处方数',
    `last_visit_date` DATE DEFAULT NULL COMMENT '最近就诊日期',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    CONSTRAINT `fk_health_record_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康档案表';

INSERT INTO `health_record` (`user_id`, `height`, `weight`, `blood_type`, `marital_status`, `health_score`, `total_visits`, `total_prescriptions`, `last_visit_date`, `created_at`) VALUES
(1, 175.0, 72.5, 'A型',  '已婚', 85, 3,  2, '2026-04-15', NOW()),
(2, 170.0, 65.0, 'O型',  '已婚', 90, 5,  3, '2026-05-01', NOW()),
(3, 162.0, 55.0, 'B型',  '已婚', 88, 4,  2, '2026-04-28', NOW()),
(4, 178.0, 80.0, 'AB型', '未婚', 72, 8,  5, '2026-05-04', NOW()),
(5, 158.0, 60.0, 'O型',  '已婚', 65, 12, 7, '2026-05-04', NOW());

-- ============================================
-- 11. health_indicator 健康指标表（依赖 sys_user）
-- ============================================
DROP TABLE IF EXISTS `health_indicator`;
CREATE TABLE `health_indicator` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '指标ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `indicator_type` VARCHAR(20) NOT NULL COMMENT '指标类型：blood_pressure-血压，blood_sugar-血糖，heart_rate-心率，temperature-体温',
    `systolic` INT DEFAULT NULL COMMENT '收缩压',
    `diastolic` INT DEFAULT NULL COMMENT '舒张压',
    `heart_rate` INT DEFAULT NULL COMMENT '心率',
    `record_date` DATE NOT NULL COMMENT '记录日期',
    `record_time` TIME NOT NULL COMMENT '记录时间',
    `remark` VARCHAR(200) DEFAULT NULL COMMENT '备注',
    `source` VARCHAR(50) DEFAULT 'self' COMMENT '来源：self-自测，device-设备，hospital-医院',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_record_date` (`record_date`),
    CONSTRAINT `fk_indicator_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康指标表';

INSERT INTO `health_indicator` (`user_id`, `indicator_type`, `systolic`, `diastolic`, `heart_rate`, `record_date`, `record_time`, `remark`, `source`, `created_at`) VALUES
(4, 'blood_pressure', 135, 88, 78, '2026-05-04', '08:30:00', '晨起测量，未服药',            'self',     NOW()),
(4, 'blood_sugar',    NULL, NULL, NULL, '2026-05-04', '07:00:00', '空腹血糖6.2mmol/L',          'device',   NOW()),
(5, 'blood_pressure', 150, 95, 82, '2026-05-04', '09:00:00', '服药后2小时测量，偏高',       'self',     NOW()),
(5, 'heart_rate',     NULL, NULL, 72, '2026-05-04', '10:00:00', '静息心率，正常范围',          'hospital', NOW()),
(4, 'blood_pressure', 128, 82, 75, '2026-05-03', '08:00:00', '服药后血压控制良好',          'self',     NOW());

-- ============================================
-- 12. allergy 过敏史表（依赖 sys_user）
-- ============================================
DROP TABLE IF EXISTS `allergy`;
CREATE TABLE `allergy` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '过敏ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `allergen` VARCHAR(200) NOT NULL COMMENT '过敏原',
    `reaction` TEXT COMMENT '过敏反应',
    `severity` VARCHAR(20) DEFAULT 'mild' COMMENT '严重程度：mild-轻度，moderate-中度，severe-重度',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_allergy_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='过敏史表';

INSERT INTO `allergy` (`user_id`, `allergen`, `reaction`, `severity`, `created_at`) VALUES
(4, '青霉素',     '皮疹、瘙痒，严重时呼吸困难',           'severe',   NOW()),
(4, '花粉',       '打喷嚏、流鼻涕、眼睛发痒',             'moderate', NOW()),
(5, '海鲜',       '皮肤红疹、腹痛腹泻',                   'moderate', NOW()),
(5, '磺胺类药物', '全身皮疹、发热',                       'severe',   NOW()),
(2, '芒果',       '口唇肿胀、轻微皮疹',                   'mild',     NOW());

-- ============================================
-- 13. past_disease 既往病史表（依赖 sys_user）
-- ============================================
DROP TABLE IF EXISTS `past_disease`;
CREATE TABLE `past_disease` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '病史ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `disease_name` VARCHAR(200) NOT NULL COMMENT '疾病名称',
    `diagnosis_date` DATE DEFAULT NULL COMMENT '确诊日期',
    `diagnosis_hospital` VARCHAR(200) DEFAULT NULL COMMENT '确诊医院',
    `current_status` VARCHAR(20) DEFAULT 'ongoing' COMMENT '当前状态：ongoing-进行中，recovered-已康复，chronic-慢性病',
    `treatment` TEXT COMMENT '治疗方案',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_past_disease_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='既往病史表';

INSERT INTO `past_disease` (`user_id`, `disease_name`, `diagnosis_date`, `diagnosis_hospital`, `current_status`, `treatment`, `remark`, `created_at`) VALUES
(4, '急性阑尾炎',     '2020-03-15', '北京协和医院',         'recovered', '腹腔镜阑尾切除术，术后恢复良好',           '已完全康复',    NOW()),
(4, '过敏性鼻炎',     '2018-09-01', '北京同仁医院',         'chronic',   '鼻喷激素+抗组胺药物',                     '季节性发作',    NOW()),
(5, '高血压',         '2021-06-20', '社区卫生服务中心',     'chronic',   '硝苯地平缓释片，每日监测血压',             '规律服药中',    NOW()),
(5, '2型糖尿病',      '2024-01-10', '北京医院',             'chronic',   '二甲双胍片，控制饮食，规律运动',           '血糖控制尚可',  NOW()),
(5, '胆囊结石',       '2019-11-05', '北京大学第一医院',     'recovered', '保守治疗后自行排出',                       '定期复查',      NOW());

-- ============================================
-- 14. family_disease 家族病史表（依赖 sys_user）
-- ============================================
DROP TABLE IF EXISTS `family_disease`;
CREATE TABLE `family_disease` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '家族病史ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `disease_name` VARCHAR(200) NOT NULL COMMENT '疾病名称',
    `relation` VARCHAR(50) NOT NULL COMMENT '亲属关系：father-父亲，mother-母亲，grandfather-祖父，grandmother-祖母，sibling-兄弟姐妹',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_family_disease_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='家族病史表';

INSERT INTO `family_disease` (`user_id`, `disease_name`, `relation`, `remark`, `created_at`) VALUES
(4, '高血压',     'father',     '父亲50岁确诊，长期服药控制',       NOW()),
(4, '冠心病',     'grandfather', '祖父因冠心病去世',                 NOW()),
(5, '糖尿病',     'mother',     '母亲45岁确诊2型糖尿病',            NOW()),
(5, '高血压',     'father',     '父亲有高血压病史20年',             NOW()),
(5, '乳腺癌',     'grandmother', '祖母60岁确诊，已治愈',            NOW());

-- ============================================
-- 15. surgical_history 手术史表（依赖 sys_user）
-- ============================================
DROP TABLE IF EXISTS `surgical_history`;
CREATE TABLE `surgical_history` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '手术ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `surgery_name` VARCHAR(200) NOT NULL COMMENT '手术名称',
    `surgery_date` DATE NOT NULL COMMENT '手术日期',
    `hospital` VARCHAR(200) DEFAULT NULL COMMENT '手术医院',
    `recovery` TEXT COMMENT '恢复情况',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_surgical_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='手术史表';

INSERT INTO `surgical_history` (`user_id`, `surgery_name`, `surgery_date`, `hospital`, `recovery`, `created_at`) VALUES
(4, '腹腔镜阑尾切除术',   '2020-03-16', '北京协和医院',         '术后3天出院，2周后恢复正常活动',           NOW()),
(4, '鼻中隔矫正术',       '2022-07-10', '北京同仁医院',         '术后1周出院，鼻腔通气明显改善',           NOW()),
(5, '剖宫产手术',         '2005-08-20', '北京妇产医院',         '术后恢复良好，无并发症',                   NOW()),
(5, '白内障摘除术（左眼）','2024-11-15', '北京大学人民医院',     '术后视力恢复至0.8，定期复查',             NOW()),
(1, '甲状腺结节消融术',   '2025-03-08', '中日友好医院',         '术后恢复良好，定期超声随访',               NOW());

-- ============================================
-- 16. visit_record 就诊记录表（依赖 sys_user, t_doctor, t_prescription）
-- ============================================
DROP TABLE IF EXISTS `visit_record`;
CREATE TABLE `visit_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '就诊ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `visit_type` VARCHAR(50) NOT NULL COMMENT '就诊类型：门诊、急诊、体检、复查',
    `visit_date` DATE NOT NULL COMMENT '就诊日期',
    `department` VARCHAR(100) DEFAULT NULL COMMENT '就诊科室',
    `doctor_id` BIGINT DEFAULT NULL COMMENT '医生ID',
    `chief_complaint` TEXT COMMENT '主诉',
    `diagnosis` TEXT COMMENT '诊断',
    `treatment` TEXT COMMENT '处理/治疗',
    `prescription_id` BIGINT DEFAULT NULL COMMENT '关联处方ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_doctor_id` (`doctor_id`),
    CONSTRAINT `fk_visit_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user`(`id`),
    CONSTRAINT `fk_visit_doctor` FOREIGN KEY (`doctor_id`) REFERENCES `t_doctor`(`doctor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='就诊记录表';

INSERT INTO `visit_record` (`user_id`, `visit_type`, `visit_date`, `department`, `doctor_id`, `chief_complaint`, `diagnosis`, `treatment`, `prescription_id`, `created_at`) VALUES
(4, '门诊', '2026-05-04', '内科',  1, '头晕、胸闷三天',             '高血压2级，冠心病待排',     '开具降压药和活血化瘀药物，建议进一步检查', 1, NOW()),
(5, '门诊', '2026-05-04', '内科',  1, '高血压复查，血压波动较大',   '高血压合并2型糖尿病',       '调整降压方案，联合用药，加强血糖监测',     4, NOW()),
(4, '门诊', '2026-04-15', '全科',  3, '感冒发热两天',               '上呼吸道感染',              '对症治疗，多休息多饮水',                   3, NOW()),
(5, '复查', '2026-04-20', '内科',  2, '慢性支气管炎复查',           '慢性支气管炎稳定期',        '继续用药，注意保暖防寒',                   2, NOW()),
(4, '体检', '2026-03-10', '全科',  3, '年度体检',                   '体检正常，轻度脂肪肝',      '建议控制饮食，增加运动',                   NULL, NOW());

-- ============================================
-- 17. health_record_authorization 健康档案授权表（依赖 sys_user）
-- ============================================
DROP TABLE IF EXISTS `health_record_authorization`;
CREATE TABLE `health_record_authorization` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '授权ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID（档案所有者）',
    `target_type` VARCHAR(20) NOT NULL COMMENT '授权对象类型：doctor-医生，hospital-医院',
    `target_id` BIGINT NOT NULL COMMENT '授权对象ID',
    `permissions` VARCHAR(200) DEFAULT NULL COMMENT '授权权限：read-只读，write-读写',
    `expires_at` DATETIME COMMENT '过期时间',
    `status` VARCHAR(20) DEFAULT 'active' COMMENT '状态：active-有效，expired-已过期，revoked-已撤销',
    `authorized_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '授权时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_authorization_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康档案授权表';

INSERT INTO `health_record_authorization` (`user_id`, `target_type`, `target_id`, `permissions`, `expires_at`, `status`, `authorized_at`) VALUES
(4, 'doctor', 1, 'read',        '2026-12-31 23:59:59', 'active',  NOW()),
(4, 'doctor', 3, 'read',        '2026-12-31 23:59:59', 'active',  NOW()),
(5, 'doctor', 1, 'read,write',  '2026-12-31 23:59:59', 'active',  NOW()),
(5, 'doctor', 2, 'read',        '2026-12-31 23:59:59', 'active',  NOW()),
(4, 'doctor', 5, 'read',        '2026-06-30 23:59:59', 'active',  NOW());

-- ============================================
-- 18. inventory 库存表（依赖 drugs）
-- ============================================
DROP TABLE IF EXISTS `inventory`;
CREATE TABLE `inventory` (
    `id` VARCHAR(36) NOT NULL COMMENT '库存ID',
    `drug_id` VARCHAR(36) NOT NULL COMMENT '药品ID',
    `stock` INT NOT NULL DEFAULT 0 COMMENT '库存数量',
    `safety_stock` INT DEFAULT 100 COMMENT '安全库存',
    `status` VARCHAR(20) DEFAULT 'normal' COMMENT '状态：normal-正常，low-库存不足，expired-已过期',
    `expiry_date` DATE COMMENT '有效期至',
    `days_to_expiry` INT COMMENT '距过期天数',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_drug_id` (`drug_id`),
    CONSTRAINT `fk_inventory_drug` FOREIGN KEY (`drug_id`) REFERENCES `drugs`(`drug_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存表';

INSERT INTO `inventory` (`id`, `drug_id`, `stock`, `safety_stock`, `status`, `expiry_date`, `days_to_expiry`, `created_at`) VALUES
('INV001', 'DRUG001', 500, 100, 'normal', '2028-05-01', 728, NOW()),
('INV002', 'DRUG002', 300, 100, 'normal', '2028-06-15', 772, NOW()),
('INV003', 'DRUG003', 200,  80, 'normal', '2028-03-20', 685, NOW()),
('INV004', 'DRUG004',  50, 100, 'low',    '2027-12-01', 576, NOW()),
('INV005', 'DRUG005', 600, 150, 'normal', '2028-08-30', 848, NOW());

-- ============================================
-- 19. doctor_schedule 医生排班表
-- ============================================
DROP TABLE IF EXISTS `doctor_schedule`;
CREATE TABLE `doctor_schedule` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '排班ID',
    `doctor_id` BIGINT NOT NULL COMMENT '医生ID',
    `department_id` BIGINT DEFAULT NULL COMMENT '科室ID',
    `work_date` DATE NOT NULL COMMENT '工作日期',
    `start_time` VARCHAR(10) NOT NULL COMMENT '开始时间',
    `end_time` VARCHAR(10) NOT NULL COMMENT '结束时间',
    `max_appointments` INT DEFAULT 20 COMMENT '最大预约数',
    `current_appointments` INT DEFAULT 0 COMMENT '当前预约数',
    `status` VARCHAR(20) DEFAULT 'active' COMMENT '状态：active-有效，cancelled-已取消，completed-已完成',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_doctor_id` (`doctor_id`),
    KEY `idx_work_date` (`work_date`),
    KEY `idx_department_id` (`department_id`),
    CONSTRAINT `fk_schedule_doctor` FOREIGN KEY (`doctor_id`) REFERENCES `t_doctor`(`doctor_id`),
    CONSTRAINT `fk_schedule_department` FOREIGN KEY (`department_id`) REFERENCES `t_department`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医生排班表';

-- ============================================
-- 20. notification 通知表
-- ============================================
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '通知ID',
    `user_id` BIGINT NOT NULL COMMENT '接收用户ID',
    `title` VARCHAR(200) NOT NULL COMMENT '通知标题',
    `content` TEXT COMMENT '通知内容',
    `type` VARCHAR(50) DEFAULT 'system' COMMENT '通知类型：system-系统，appointment-预约，consultation-问诊，prescription-处方',
    `is_read` TINYINT DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
    `related_id` BIGINT DEFAULT NULL COMMENT '关联业务ID',
    `related_type` VARCHAR(50) DEFAULT NULL COMMENT '关联业务类型',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_is_read` (`is_read`),
    CONSTRAINT `fk_notification_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';

-- ============================================
-- 21. inventory_record 库存变动记录表
-- ============================================
DROP TABLE IF EXISTS `inventory_record`;
CREATE TABLE `inventory_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `drug_id` VARCHAR(36) NOT NULL COMMENT '药品ID',
    `inventory_id` VARCHAR(36) DEFAULT NULL COMMENT '库存ID',
    `change_type` VARCHAR(20) NOT NULL COMMENT '变动类型：in-入库，out-出库，adjust-调整',
    `change_quantity` INT NOT NULL COMMENT '变动数量',
    `before_quantity` INT NOT NULL COMMENT '变动前库存',
    `after_quantity` INT NOT NULL COMMENT '变动后库存',
    `operator_id` BIGINT DEFAULT NULL COMMENT '操作人ID',
    `reason` VARCHAR(500) DEFAULT NULL COMMENT '变动原因',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_drug_id` (`drug_id`),
    KEY `idx_inventory_id` (`inventory_id`),
    CONSTRAINT `fk_record_drug` FOREIGN KEY (`drug_id`) REFERENCES `drugs`(`drug_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存变动记录表';

-- ============================================
-- 22. operation_log 操作日志表
-- ============================================
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `user_id` BIGINT DEFAULT NULL COMMENT '操作人ID',
    `username` VARCHAR(50) DEFAULT NULL COMMENT '操作人用户名',
    `role` VARCHAR(20) DEFAULT NULL COMMENT '操作人角色',
    `operation_type` VARCHAR(50) NOT NULL COMMENT '操作类型',
    `target_type` VARCHAR(50) DEFAULT NULL COMMENT '操作对象类型',
    `target_id` BIGINT DEFAULT NULL COMMENT '操作对象ID',
    `content` VARCHAR(500) DEFAULT NULL COMMENT '操作内容',
    `ip_address` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    `user_agent` VARCHAR(500) DEFAULT NULL COMMENT 'User-Agent',
    `status` VARCHAR(20) DEFAULT 'success' COMMENT '操作状态：success-成功，fail-失败',
    `error_msg` TEXT COMMENT '错误信息',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_operation_type` (`operation_type`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- ============================================
-- 完成！共 22 张表，每表插入测试数据
-- 测试账号密码均为 123456
-- 管理员：admin
-- 医生：doctor_zs（张三）、doctor_ls（李四）
-- 患者：user_w5（王五）、user_zl（赵六）
-- ============================================
