-- 药品管理系统MySQL建表语句

-- 1. 药品表 (drugs)
CREATE TABLE IF NOT EXISTS drugs (
    id VARCHAR(50) PRIMARY KEY COMMENT '药品ID',
    drug_name VARCHAR(100) NOT NULL COMMENT '药品名称',
    generic_name VARCHAR(100) NOT NULL COMMENT '通用名称',
    specification VARCHAR(100) NOT NULL COMMENT '规格',
    manufacturer VARCHAR(100) NOT NULL COMMENT '生产厂家',
    category VARCHAR(50) NOT NULL COMMENT '分类',
    type ENUM('western', 'chinese') NOT NULL COMMENT '类型：西药或中成药',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    stock INT NOT NULL COMMENT '库存',
    unit VARCHAR(20) NOT NULL COMMENT '单位',
    english_name VARCHAR(200) COMMENT '英文名称',
    approval_no VARCHAR(50) COMMENT '批准文号',
    form VARCHAR(50) COMMENT '剂型',
    shelf_life VARCHAR(50) COMMENT '保质期',
    storage VARCHAR(200) COMMENT '存储条件',
    indications TEXT COMMENT '适应症',
    contraindications TEXT COMMENT '禁忌症',
    adverse_reactions TEXT COMMENT '不良反应',
    precautions TEXT COMMENT '注意事项',
    drug_interactions TEXT COMMENT '药物相互作用',
    dosage TEXT COMMENT '用法用量',
    image VARCHAR(500) COMMENT '药品图片URL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='药品信息表';

-- 2. 库存表 (inventory)
CREATE TABLE IF NOT EXISTS inventory (
    id VARCHAR(50) PRIMARY KEY COMMENT '库存ID',
    drug_id VARCHAR(50) NOT NULL COMMENT '药品ID',
    stock INT NOT NULL COMMENT '库存数量',
    safety_stock INT NOT NULL COMMENT '安全库存',
    status ENUM('normal', 'low', 'out') NOT NULL COMMENT '状态：正常、不足、缺货',
    expiry_date DATE NOT NULL COMMENT '有效期至',
    days_to_expiry INT COMMENT '剩余天数',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (drug_id) REFERENCES drugs(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='库存信息表';

-- 3. 药品分类表 (drug_categories)
CREATE TABLE IF NOT EXISTS drug_categories (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '分类名称',
    description VARCHAR(200) COMMENT '分类描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='药品分类表';

-- 插入初始分类数据
INSERT INTO drug_categories (name, description) VALUES
('心血管系统用药', '用于治疗心血管系统疾病的药物'),
('呼吸系统用药', '用于治疗呼吸系统疾病的药物'),
('消化系统用药', '用于治疗消化系统疾病的药物'),
('抗生素', '用于治疗细菌感染的药物'),
('神经系统用药', '用于治疗神经系统疾病的药物'),
('内分泌系统用药', '用于治疗内分泌系统疾病的药物'),
('抗肿瘤用药', '用于治疗肿瘤的药物'),
('维生素与矿物质', '补充维生素和矿物质的药物');

-- 插入示例药品数据
INSERT INTO drugs (id, drug_name, generic_name, specification, manufacturer, category, type, price, stock, unit, english_name, approval_no, form, shelf_life, storage, indications, contraindications, adverse_reactions, precautions, drug_interactions, dosage, image) VALUES
('drug_001', '氨氯地平片', '苯磺酸氨氯地平', '5mg*28片', '辉瑞制药', '心血管系统用药', 'western', 45.50, 100, '盒', 'Amlodipine Besylate Tablets', '国药准字H20020390', '片剂', '36个月', '遮光，密封保存', '1.高血压。2.慢性稳定性心绞痛及变异型心绞痛。', '对二氢吡啶类钙拮抗剂过敏者禁用。', '头痛、水肿、疲劳、失眠、恶心、腹痛、面红、心悸和头晕。', '1.肝功能受损患者慎用。2.肾功能衰竭患者可采用正常剂量。', '与西咪替丁、葡萄柚汁等合用可能增加血药浓度。', '治疗高血压的初始剂量为5mg，每日一次。', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=medicine%20tablet%20bottle%20with%20label&image_size=square'),
('drug_002', '厄贝沙坦片', '厄贝沙坦', '150mg*14片', '赛诺菲', '心血管系统用药', 'western', 65.50, 50, '盒', 'Irbesartan Tablets', '国药准字H20000545', '片剂', '36个月', '密封保存', '高血压。', '对本品成分过敏者禁用。', '头痛、眩晕、心悸等，偶有咳嗽。', '肾功能不全患者可能需要减少剂量。', '与利尿剂合用时应注意血容量不足或因低钠可引起低血压。', '初始剂量为150mg，每日一次。', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=medicine%20tablet%20bottle%20with%20label&image_size=square'),
('drug_003', '阿莫西林胶囊', '阿莫西林', '0.25g*24粒', '哈药集团', '抗生素', 'western', 25.80, 200, '盒', 'Amoxicillin Capsules', '国药准字H23023294', '胶囊剂', '24个月', '密封，在凉暗干燥处保存', '敏感菌所致的感染，如中耳炎、鼻窦炎、咽炎、扁桃体炎等上呼吸道感染。', '对青霉素类药物过敏者禁用。', '恶心、呕吐、腹泻等胃肠道反应。', '青霉素皮试阳性反应者禁用。', '与丙磺舒合用可延长其半衰期。', '成人一次1-2粒，一日3次。', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=medicine%20capsule%20bottle&image_size=square');

-- 插入示例库存数据
INSERT INTO inventory (id, drug_id, stock, safety_stock, status, expiry_date, days_to_expiry) VALUES
('inv_001', 'drug_001', 100, 20, 'normal', '2028-12-01', 968),
('inv_002', 'drug_002', 15, 20, 'low', '2027-10-15', 556),
('inv_003', 'drug_003', 0, 50, 'out', '2028-06-30', 821);

-- 创建索引
CREATE INDEX idx_drugs_category ON drugs(category);
CREATE INDEX idx_drugs_type ON drugs(type);
CREATE INDEX idx_inventory_drug_id ON inventory(drug_id);
CREATE INDEX idx_inventory_status ON inventory(status);
CREATE INDEX idx_inventory_expiry_date ON inventory(expiry_date);
