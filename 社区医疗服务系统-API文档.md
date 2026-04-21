# 社区医疗服务系统 API 文档

## 文档信息

| 项目 | 内容 |
|------|------|
| 版本 | v1.0.0 |
| 创建日期 | 2026-04-08 |
| 协议 | HTTPS |
| 数据格式 | JSON |
| 编码 | UTF-8 |
| 时间格式 | ISO 8601 |

---

## 目录

1. [通用规范](#1-通用规范)
2. [认证授权](#2-认证授权)
3. [用户管理模块](#3-用户管理模块)
4. [健康档案模块](#4-健康档案模块)
5. [预约挂号模块](#5-预约挂号模块)
6. [在线问诊模块](#6-在线问诊模块)
7. [处方管理模块](#7-处方管理模块)
8. [药品管理模块](#8-药品管理模块)
9. [慢病管理模块](#9-慢病管理模块)
10. [健康教育模块](#10-健康教育模块)
11. [数据统计模块](#11-数据统计模块)

---

## 1. 通用规范

### 1.1 请求规范

**基础URL**
```
生产环境: https://api.community-health.com/v1
测试环境: https://api-test.community-health.com/v1
```

**请求头**
```http
Content-Type: application/json
Authorization: Bearer {access_token}
X-Request-ID: {uuid}
X-Client-Version: 1.0.0
```

### 1.2 响应规范

**成功响应**
```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": "2026-04-08T10:30:00Z",
  "requestId": "uuid"
}
```

**错误响应**
```json
{
  "code": 400,
  "message": "参数错误",
  "data": null,
  "error": {
    "type": "ValidationError",
    "details": [
      {
        "field": "phone",
        "message": "手机号格式不正确"
      }
    ]
  },
  "timestamp": "2026-04-08T10:30:00Z",
  "requestId": "uuid"
}
```

### 1.3 状态码

| 状态码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 201 | 创建成功 |
| 204 | 删除成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 409 | 资源冲突 |
| 422 | 业务逻辑错误 |
| 429 | 请求过于频繁 |
| 500 | 服务器内部错误 |

### 1.4 分页规范

**请求参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码，默认1 |
| size | int | 否 | 每页数量，默认20，最大100 |
| sort | string | 否 | 排序字段，如：createdAt:desc |

**响应结构**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [],
    "pagination": {
      "page": 1,
      "size": 20,
      "total": 100,
      "totalPages": 5,
      "hasNext": true,
      "hasPrev": false
    }
  }
}
```

---

## 2. 认证授权

### 2.1 用户注册

**接口信息**
- **URL**: `/auth/register`
- **Method**: POST
- **描述**: 新用户注册

**请求参数**
```json
{
  "phone": "13800138000",
  "password": "encrypted_password",
  "idCard": "110101199001011234",
  "realName": "张三",
  "smsCode": "123456",
  "inviteCode": ""
}
```

**响应示例**
```json
{
  "code": 201,
  "message": "注册成功",
  "data": {
    "userId": "usr_1234567890",
    "phone": "13800138000",
    "realName": "张三",
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
    "expiresIn": 7200
  }
}
```

### 2.2 用户登录

**接口信息**
- **URL**: `/auth/login`
- **Method**: POST
- **描述**: 用户登录

**请求参数**
```json
{
  "account": "13800138000",
  "password": "encrypted_password",
  "loginType": "password",
  "deviceId": "device_uuid",
  "deviceType": "ios"
}
```

**响应示例**
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "userId": "usr_1234567890",
    "phone": "13800138000",
    "realName": "张三",
    "avatar": "https://...",
    "role": "resident",
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
    "expiresIn": 7200
  }
}
```

### 2.3 短信验证码登录

**接口信息**
- **URL**: `/auth/login/sms`
- **Method**: POST

**请求参数**
```json
{
  "phone": "13800138000",
  "smsCode": "123456",
  "deviceId": "device_uuid"
}
```

### 2.4 发送短信验证码

**接口信息**
- **URL**: `/auth/sms-code`
- **Method**: POST

**请求参数**
```json
{
  "phone": "13800138000",
  "type": "login",
  "captchaToken": "captcha_token"
}
```

### 2.5 刷新Token

**接口信息**
- **URL**: `/auth/refresh`
- **Method**: POST

**请求参数**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
}
```

**响应示例**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
    "expiresIn": 7200
  }
}
```

### 2.6 退出登录

**接口信息**
- **URL**: `/auth/logout`
- **Method**: POST

**响应示例**
```json
{
  "code": 200,
  "message": "退出成功",
  "data": null
}
```

---

## 3. 用户管理模块

### 3.1 获取当前用户信息

**接口信息**
- **URL**: `/users/me`
- **Method**: GET

**响应示例**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": "usr_1234567890",
    "phone": "13800138000",
    "realName": "张三",
    "idCard": "110101199001011234",
    "avatar": "https://...",
    "gender": "male",
    "birthDate": "1990-01-01",
    "age": 36,
    "address": {
      "province": "北京市",
      "city": "北京市",
      "district": "朝阳区",
      "street": "建国路",
      "detail": "1号院1号楼101"
    },
    "emergencyContact": {
      "name": "李四",
      "phone": "13900139000",
      "relation": "配偶"
    },
    "medicalInsurance": {
      "cardNo": "110101199001011234",
      "type": "城镇职工",
      "status": "正常"
    },
    "role": "resident",
    "createdAt": "2026-01-01T00:00:00Z",
    "updatedAt": "2026-04-08T10:30:00Z"
  }
}
```

### 3.2 更新用户信息

**接口信息**
- **URL**: `/users/me`
- **Method**: PUT

**请求参数**
```json
{
  "realName": "张三",
  "avatar": "https://...",
  "gender": "male",
  "birthDate": "1990-01-01",
  "address": {
    "province": "北京市",
    "city": "北京市",
    "district": "朝阳区",
    "street": "建国路",
    "detail": "1号院1号楼101"
  },
  "emergencyContact": {
    "name": "李四",
    "phone": "13900139000",
    "relation": "配偶"
  }
}
```

### 3.3 修改密码

**接口信息**
- **URL**: `/users/me/password`
- **Method**: PUT

**请求参数**
```json
{
  "oldPassword": "encrypted_old_password",
  "newPassword": "encrypted_new_password"
}
```

### 3.4 绑定医保卡

**接口信息**
- **URL**: `/users/me/medical-insurance`
- **Method**: POST

**请求参数**
```json
{
  "cardNo": "110101199001011234",
  "password": "encrypted_password"
}
```

### 3.5 获取用户列表（管理员）

**接口信息**
- **URL**: `/users`
- **Method**: GET
- **权限**: admin

**查询参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | string | 否 | 搜索关键词（姓名/手机号） |
| role | string | 否 | 角色筛选 |
| status | string | 否 | 状态筛选 |
| startDate | string | 否 | 注册开始日期 |
| endDate | string | 否 | 注册结束日期 |

**响应示例**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "userId": "usr_1234567890",
        "phone": "13800138000",
        "realName": "张三",
        "role": "resident",
        "status": "active",
        "createdAt": "2026-01-01T00:00:00Z"
      }
    ],
    "pagination": {
      "page": 1,
      "size": 20,
      "total": 100
    }
  }
}
```

---

## 4. 健康档案模块

### 4.1 获取健康档案概览

**接口信息**
- **URL**: `/health-records/overview`
- **Method**: GET

**响应示例**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": "usr_1234567890",
    "basicInfo": {
      "height": 175,
      "weight": 70,
      "bmi": 22.9,
      "bloodType": "A",
      "maritalStatus": "married"
    },
    "medicalHistory": {
      "pastDiseases": ["高血压"],
      "familyDiseases": ["糖尿病"],
      "allergies": ["青霉素"],
      "surgicalHistory": ["阑尾切除术"]
    },
    "statistics": {
      "totalVisits": 15,
      "totalPrescriptions": 23,
      "lastVisitDate": "2026-03-15",
      "healthScore": 85
    }
  }
}
```

### 4.2 获取病史记录

**接口信息**
- **URL**: `/health-records/medical-history`
- **Method**: GET

**响应示例**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "pastDiseases": [
      {
        "id": "disease_001",
        "diseaseName": "高血压",
        "diagnosisDate": "2020-05-01",
        "diagnosisHospital": "朝阳医院",
        "currentStatus": "控制中",
        "treatment": "药物治疗",
        "remark": "每日服药"
      }
    ],
    "familyDiseases": [
      {
        "id": "family_001",
        "diseaseName": "糖尿病",
        "relation": "父亲",
        "remark": "2型糖尿病"
      }
    ],
    "allergies": [
      {
        "id": "allergy_001",
        "allergen": "青霉素",
        "reaction": "皮疹",
        "severity": "中等"
      }
    ],
    "surgicalHistory": [
      {
        "id": "surgery_001",
        "surgeryName": "阑尾切除术",
        "surgeryDate": "2015-03-10",
        "hospital": "协和医院",
        "recovery": "良好"
      }
    ]
  }
}
```

### 4.3 更新病史记录

**接口信息**
- **URL**: `/health-records/medical-history`
- **Method**: PUT

**请求参数**
```json
{
  "pastDiseases": [
    {
      "diseaseName": "高血压",
      "diagnosisDate": "2020-05-01",
      "diagnosisHospital": "朝阳医院",
      "currentStatus": "控制中",
      "treatment": "药物治疗"
    }
  ],
  "allergies": [
    {
      "allergen": "青霉素",
      "reaction": "皮疹",
      "severity": "中等"
    }
  ]
}
```

### 4.4 获取就诊记录列表

**接口信息**
- **URL**: `/health-records/visits`
- **Method**: GET

**查询参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| type | string | 否 | 就诊类型（outpatient/inpatient） |
| startDate | string | 否 | 开始日期 |
| endDate | string | 否 | 结束日期 |
| doctorId | string | 否 | 医生ID |

**响应示例**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "visitId": "visit_001",
        "visitType": "outpatient",
        "visitDate": "2026-03-15",
        "department": "内科",
        "doctor": {
          "doctorId": "doc_001",
          "name": "王医生",
          "title": "主任医师"
        },
        "chiefComplaint": "头痛、头晕",
        "diagnosis": "高血压",
        "treatment": "调整用药",
        "prescriptionId": "pres_001",
        "attachments": [
          {
            "fileId": "file_001",
            "fileName": "检查报告.pdf",
            "fileType": "pdf",
            "fileUrl": "https://..."
          }
        ]
      }
    ],
    "pagination": {
      "page": 1,
      "size": 10,
      "total": 15
    }
  }
}
```

### 4.5 获取就诊记录详情

**接口信息**
- **URL**: `/health-records/visits/{visitId}`
- **Method**: GET

**响应示例**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "visitId": "visit_001",
    "visitType": "outpatient",
    "visitDate": "2026-03-15T09:30:00Z",
    "department": "内科",
    "doctor": {
      "doctorId": "doc_001",
      "name": "王医生",
      "title": "主任医师",
      "avatar": "https://..."
    },
    "chiefComplaint": "头痛、头晕一周",
    "presentIllness": "患者一周前出现头痛、头晕症状...",
    "pastHistory": "高血压病史5年",
    "physicalExamination": {
      "bloodPressure": "150/95mmHg",
      "heartRate": 78,
      "temperature": 36.5,
      "weight": 70
    },
    "auxiliaryExamination": "血常规、血脂检查",
    "diagnosis": "高血压病2级 中危",
    "treatment": "调整降压药物",
    "prescription": {
      "prescriptionId": "pres_001",
      "drugs": [
        {
          "drugName": "氨氯地平片",
          "specification": "5mg*28片",
          "dosage": "每日一次，每次一片",
          "quantity": 2,
          "unit": "盒"
        }
      ]
    },
    "doctorAdvice": "低盐饮食，定期监测血压",
    "followUp": "2周后复诊",
    "attachments": [],
    "createdAt": "2026-03-15T10:00:00Z"
  }
}
```

### 4.6 获取健康指标记录

**接口信息**
- **URL**: `/health-records/indicators`
- **Method**: GET

**查询参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| type | string | 是 | 指标类型（bloodPressure/bloodSugar/weight/height） |
| startDate | string | 否 | 开始日期 |
| endDate | string | 否 | 结束日期 |

**响应示例**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "indicatorType": "bloodPressure",
    "unit": "mmHg",
    "statistics": {
      "latest": {
        "systolic": 135,
        "diastolic": 85,
        "recordDate": "2026-04-08",
        "recordTime": "08:30"
      },
      "average": {
        "systolic": 138,
        "diastolic": 88
      },
      "max": {
        "systolic": 150,
        "diastolic": 95
      },
      "min": {
        "systolic": 125,
        "diastolic": 80
      }
    },
    "records": [
      {
        "recordId": "rec_001",
        "systolic": 135,
        "diastolic": 85,
        "heartRate": 72,
        "recordDate": "2026-04-08",
        "recordTime": "08:30",
        "remark": "晨起测量",
        "source": "manual"
      }
    ]
  }
}
```

### 4.7 添加健康指标记录

**接口信息**
- **URL**: `/health-records/indicators`
- **Method**: POST

**请求参数**
```json
{
  "indicatorType": "bloodPressure",
  "systolic": 135,
  "diastolic": 85,
  "heartRate": 72,
  "recordDate": "2026-04-08",
  "recordTime": "08:30",
  "remark": "晨起测量",
  "source": "manual"
}
```

### 4.8 档案授权管理

**接口信息**
- **URL**: `/health-records/authorizations`
- **Method**: GET

**响应示例**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "authorizedDoctors": [
      {
        "authorizationId": "auth_001",
        "doctorId": "doc_001",
        "doctorName": "王医生",
        "department": "内科",
        "authorizedAt": "2026-01-01T00:00:00Z",
        "expiresAt": "2027-01-01T00:00:00Z",
        "status": "active",
        "permissions": ["view", "update"]
      }
    ],
    "authorizedFamily": [
      {
        "authorizationId": "auth_002",
        "userId": "usr_0987654321",
        "realName": "李四",
        "relation": "配偶",
        "authorizedAt": "2026-01-01T00:00:00Z",
        "permissions": ["view"]
      }
    ]
  }
}
```

**添加授权**
- **URL**: `/health-records/authorizations`
- **Method**: POST

**请求参数**
```json
{
  "targetType": "doctor",
  "targetId": "doc_001",
  "permissions": ["view"],
  "expiresAt": "2027-01-01T00:00:00Z"
}
```

---

## 5. 预约挂号模块

### 5.1 获取科室列表

**接口信息**
- **URL**: `/departments`
- **Method**: GET

**响应示例**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "departmentId": "dept_001",
        "name": "内科",
        "description": "心血管、呼吸、消化等内科疾病",
        "icon": "https://...",
        "doctorsCount": 5,
        "children": [
          {
            "departmentId": "dept_001_1",
            "name": "心血管内科",
            "parentId": "dept_001"
          }
        ]
      }
    ]
  }
}
```

### 5.2 获取医生列表

**接口信息**
- **URL**: `/doctors`
- **Method**: GET

**查询参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| departmentId | string | 否 | 科室ID |
| date | string | 否 | 出诊日期 |
| keyword | string | 否 | 搜索关键词 |

**响应示例**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "doctorId": "doc_001",
        "name": "王医生",
        "avatar": "https://...",
        "title": "主任医师",
        "department": {
          "departmentId": "dept_001",
          "name": "内科"
        },
        "specialty": ["高血压", "冠心病"],
        "introduction": "从事心血管内科工作20年...",
        "rating": 4.9,
        "consultationCount": 5000,
        "isAvailable": true
      }
    ],
    "pagination": {
      "page": 1,
      "size": 20,
      "total": 15
    }
  }
}
```

### 5.3 获取医生详情

**接口信息**
- **URL**: `/doctors/{doctorId}`
- **Method**: GET

**响应示例**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "doctorId": "doc_001",
    "name": "王医生",
    "avatar": "https://...",
    "title": "主任医师",
    "department": {
      "departmentId": "dept_001",
      "name": "内科"
    },
    "specialty": ["高血压", "冠心病", "心律失常"],
    "introduction": "从事心血管内科工作20年，擅长高血压、冠心病的诊治...",
    "education": "医学博士",
    "experience": "20年",
    "rating": 4.9,
    "consultationCount": 5000,
    "schedule": [
      {
        "date": "2026-04-09",
        "weekday": "周三",
        "timeSlots": [
          {
            "slotId": "slot_001",
            "startTime": "08:00",
            "endTime": "08:30",
            "total": 20,
            "available": 5,
            "fee": 50
          }
        ]
      }
    ]
  }
}
```

### 5.4 获取号源信息

**接口信息**
- **URL**: `/schedules`
- **Method**: GET

**查询参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| doctorId | string | 是 | 医生ID |
| startDate | string | 是 | 开始日期 |
| endDate | string | 是 | 结束日期 |

**响应示例**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "doctorId": "doc_001",
    "doctorName": "王医生",
    "schedules": [
      {
        "date": "2026-04-09",
        "weekday": "周三",
        "isWorkday": true,
        "timeSlots": [
          {
            "slotId": "slot_001",
            "startTime": "08:00",
            "endTime": "08:30",
            "total": 20,
            "available": 5,
            "fee": 50,
            "type": "expert"
          },
          {
            "slotId": "slot_002",
            "startTime": "08:30",
            "endTime": "09:00",
            "total": 20,
            "available": 0,
            "fee": 50,
            "type": "expert"
          }
        ]
      }
    ]
  }
}
```

### 5.5 预约挂号

**接口信息**
- **URL**: `/appointments`
- **Method**: POST

**请求参数**
```json
{
  "doctorId": "doc_001",
  "slotId": "slot_001",
  "appointmentDate": "2026-04-09",
  "patientId": "usr_1234567890",
  "visitType": "first",
  "symptom": "头痛、头晕",
  "remark": ""
}
```

**响应示例**
```json
{
  "code": 201,
  "message": "预约成功",
  "data": {
    "appointmentId": "apt_001",
    "appointmentNo": "A20260409001",
    "status": "confirmed",
    "doctor": {
      "doctorId": "doc_001",
      "name": "王医生",
      "title": "主任医师",
      "department": "内科"
    },
    "appointmentDate": "2026-04-09",
    "timeSlot": {
      "startTime": "08:00",
      "endTime": "08:30"
    },
    "patient": {
      "patientId": "usr_1234567890",
      "name": "张三",
      "phone": "13800138000"
    },
    "fee": 50,
    "location": "门诊楼3楼内科诊室301",
    "createdAt": "2026-04-08T10:30:00Z",
    "qrCode": "https://..."
  }
}
```

### 5.6 获取预约列表

**接口信息**
- **URL**: `/appointments`
- **Method**: GET

**查询参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| status | string | 否 | 状态（pending/confirmed/completed/cancelled） |
| startDate | string | 否 | 开始日期 |
| endDate | string | 否 | 结束日期 |

**响应示例**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "appointmentId": "apt_001",
        "appointmentNo": "A20260409001",
        "status": "confirmed",
        "doctor": {
          "doctorId": "doc_001",
          "name": "王医生",
          "title": "主任医师",
          "department": "内科"
        },
        "appointmentDate": "2026-04-09",
        "timeSlot": {
          "startTime": "08:00",
          "endTime": "08:30"
        },
        "fee": 50,
        "location": "门诊楼3楼内科诊室301",
        "createdAt": "2026-04-08T10:30:00Z"
      }
    ],
    "pagination": {
      "page": 1,
      "size": 10,
      "total": 5
    }
  }
}
```

### 5.7 获取预约详情

**接口信息**
- **URL**: `/appointments/{appointmentId}`
- **Method**: GET

### 5.8 取消预约

**接口信息**
- **URL**: `/appointments/{appointmentId}/cancel`
- **Method**: POST

**请求参数**
```json
{
  "reason": "临时有事",
  "refundRequired": true
}
```

### 5.9 获取排队信息

**接口信息**
- **URL**: `/appointments/{appointmentId}/queue`
- **Method**: GET

**响应示例**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "appointmentId": "apt_001",
    "appointmentNo": "A20260409001",
    "currentNumber": 15,
    "myNumber": 18,
    "waitingCount": 3,
    "estimatedTime": "15分钟",
    "status": "waiting",
    "doctor": {
      "doctorId": "doc_001",
      "name": "王医生",
      "currentPatient": "第15号"
    }
  }
}
```

---

## 6. 在线问诊模块

### 6.1 发起图文问诊

**接口信息**
- **URL**: `/consultations`
- **Method**: POST

**请求参数**
```json
{
  "doctorId": "doc_001",
  "consultationType": "text",
  "symptom": "头痛、头晕",
  "description": "最近一周经常头痛、头晕，血压偏高...",
  "images": [
    {
      "fileId": "file_001",
      "fileUrl": "https://..."
    }
  ],
  "medicalHistory": "高血压病史5年",
  "currentMedication": "氨氯地平片"
}
```

**响应示例**
```json
{
  "code": 201,
  "message": "问诊发起成功",
  "data": {
    "consultationId": "cons_001",
    "consultationNo": "C20260408001",
    "status": "pending",
    "doctor": {
      "doctorId": "doc_001",
      "name": "王医生",
      "title": "主任医师",
      "avatar": "https://..."
    },
    "patient": {
      "patientId": "usr_1234567890",
      "name": "张三"
    },
    "symptom": "头痛、头晕",
    "description": "最近一周经常头痛、头晕...",
    "fee": 30,
    "createdAt": "2026-04-08T10:30:00Z",
    "expireAt": "2026-04-09T10:30:00Z"
  }
}
```

### 6.2 获取问诊列表

**接口信息**
- **URL**: `/consultations`
- **Method**: GET

**查询参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| status | string | 否 | 状态（pending/in_progress/completed/closed） |
| type | string | 否 | 类型（text/video） |
| role | string | 否 | 角色（patient/doctor） |

**响应示例**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "consultationId": "cons_001",
        "consultationNo": "C20260408001",
        "status": "in_progress",
        "consultationType": "text",
        "doctor": {
          "doctorId": "doc_001",
          "name": "王医生",
          "title": "主任医师",
          "avatar": "https://..."
        },
        "symptom": "头痛、头晕",
        "lastMessage": {
          "content": "建议监测血压",
          "sendTime": "2026-04-08T10:35:00Z",
          "sender": "doctor"
        },
        "unreadCount": 1,
        "createdAt": "2026-04-08T10:30:00Z"
      }
    ],
    "pagination": {
      "page": 1,
      "size": 10,
      "total": 3
    }
  }
}
```

### 6.3 获取问诊详情

**接口信息**
- **URL**: `/consultations/{consultationId}`
- **Method**: GET

**响应示例**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "consultationId": "cons_001",
    "consultationNo": "C20260408001",
    "status": "in_progress",
    "consultationType": "text",
    "doctor": {
      "doctorId": "doc_001",
      "name": "王医生",
      "title": "主任医师",
      "avatar": "https://...",
      "department": "内科"
    },
    "patient": {
      "patientId": "usr_1234567890",
      "name": "张三",
      "age": 36,
      "gender": "male"
    },
    "symptom": "头痛、头晕",
    "description": "最近一周经常头痛、头晕，血压偏高...",
    "images": [
      {
        "fileId": "file_001",
        "fileUrl": "https://...",
        "thumbnail": "https://..."
      }
    ],
    "medicalHistory": "高血压病史5年",
    "currentMedication": "氨氯地平片",
    "messages": [
      {
        "messageId": "msg_001",
        "sender": "patient",
        "senderName": "张三",
        "content": "医生您好，我最近一周经常头痛、头晕...",
        "messageType": "text",
        "sendTime": "2026-04-08T10:30:00Z",
        "isRead": true
      },
      {
        "messageId": "msg_002",
        "sender": "doctor",
        "senderName": "王医生",
        "content": "您好，请问血压具体是多少？",
        "messageType": "text",
        "sendTime": "2026-04-08T10:32:00Z",
        "isRead": true
      }
    ],
    "prescription": null,
    "advice": null,
    "fee": 30,
    "createdAt": "2026-04-08T10:30:00Z",
    "expireAt": "2026-04-09T10:30:00Z"
  }
}
```

### 6.4 发送消息

**接口信息**
- **URL**: `/consultations/{consultationId}/messages`
- **Method**: POST

**请求参数**
```json
{
  "content": "血压150/95mmHg",
  "messageType": "text",
  "images": []
}
```

**响应示例**
```json
{
  "code": 201,
  "message": "发送成功",
  "data": {
    "messageId": "msg_003",
    "sender": "patient",
    "content": "血压150/95mmHg",
    "messageType": "text",
    "sendTime": "2026-04-08T10:40:00Z",
    "isRead": false
  }
}
```

### 6.5 结束问诊

**接口信息**
- **URL**: `/consultations/{consultationId}/close`
- **Method**: POST

**请求参数**
```json
{
  "reason": "问题已解决",
  "rating": 5,
  "comment": "医生很专业，解答很详细"
}
```

### 6.6 预约视频问诊

**接口信息**
- **URL**: `/consultations/video/appointments`
- **Method**: POST

**请求参数**
```json
{
  "doctorId": "doc_001",
  "appointmentDate": "2026-04-09",
  "timeSlot": "14:00-14:30",
  "symptom": "头痛、头晕",
  "description": "需要视频咨询"
}
```

---

## 7. 处方管理模块

### 7.1 获取处方列表

**接口信息**
- **URL**: `/prescriptions`
- **Method**: GET

**查询参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| status | string | 否 | 状态（pending/approved/rejected/completed） |
| startDate | string | 否 | 开始日期 |
| endDate | string | 否 | 结束日期 |

**响应示例**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "prescriptionId": "pres_001",
        "prescriptionNo": "P20260408001",
        "status": "approved",
        "type": "outpatient",
        "doctor": {
          "doctorId": "doc_001",
          "name": "王医生",
          "title": "主任医师",
          "department": "内科"
        },
        "diagnosis": "高血压",
        "drugCount": 2,
        "totalAmount": 156.5,
        "createdAt": "2026-04-08T10:30:00Z",
        "validUntil": "2026-04-15T10:30:00Z"
      }
    ],
    "pagination": {
      "page": 1,
      "size": 10,
      "total": 8
    }
  }
}
```

### 7.2 获取处方详情

**接口信息**
- **URL**: `/prescriptions/{prescriptionId}`
- **Method**: GET

**响应示例**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "prescriptionId": "pres_001",
    "prescriptionNo": "P20260408001",
    "status": "approved",
    "type": "outpatient",
    "doctor": {
      "doctorId": "doc_001",
      "name": "王医生",
      "title": "主任医师",
      "department": "内科",
      "signature": "https://..."
    },
    "patient": {
      "patientId": "usr_1234567890",
      "name": "张三",
      "gender": "male",
      "age": 36,
      "weight": 70
    },
    "diagnosis": "高血压病2级 中危",
    "diagnosisCode": "I10",
    "drugs": [
      {
        "drugId": "drug_001",
        "drugName": "氨氯地平片",
        "specification": "5mg*28片",
        "manufacturer": "辉瑞制药",
        "dosage": "每日一次，每次一片",
        "usage": "口服",
        "quantity": 2,
        "unit": "盒",
        "unitPrice": 45.5,
        "amount": 91.0,
        "remark": "晨起服用"
      },
      {
        "drugId": "drug_002",
        "drugName": "厄贝沙坦片",
        "specification": "150mg*14片",
        "manufacturer": "赛诺菲",
        "dosage": "每日一次，每次一片",
        "usage": "口服",
        "quantity": 1,
        "unit": "盒",
        "unitPrice": 65.5,
        "amount": 65.5,
        "remark": ""
      }
    ],
    "totalAmount": 156.5,
    "doctorAdvice": "低盐饮食，定期监测血压，2周后复诊",
    "pharmacist": {
      "pharmacistId": "pha_001",
      "name": "李药师",
      "reviewResult": "approved",
      "reviewTime": "2026-04-08T10:35:00Z"
    },
    "createdAt": "2026-04-08T10:30:00Z",
    "validUntil": "2026-04-15T10:30:00Z",
    "qrCode": "https://..."
  }
}
```

### 7.3 开具处方（医生）

**接口信息**
- **URL**: `/prescriptions`
- **Method**: POST
- **权限**: doctor

**请求参数**
```json
{
  "patientId": "usr_1234567890",
  "consultationId": "cons_001",
  "diagnosis": "高血压病2级 中危",
  "diagnosisCode": "I10",
  "drugs": [
    {
      "drugId": "drug_001",
      "dosage": "每日一次，每次一片",
      "usage": "口服",
      "quantity": 2,
      "unit": "盒",
      "remark": "晨起服用"
    }
  ],
  "doctorAdvice": "低盐饮食，定期监测血压"
}
```

### 7.4 审核处方（药师）

**接口信息**
- **URL**: `/prescriptions/{prescriptionId}/review`
- **Method**: POST
- **权限**: pharmacist

**请求参数**
```json
{
  "result": "approved",
  "comment": "处方合理"
}
```

### 7.5 下载处方

**接口信息**
- **URL**: `/prescriptions/{prescriptionId}/download`
- **Method**: GET

**查询参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| format | string | 否 | 格式（pdf/image），默认pdf |

---

## 8. 药品管理模块

### 8.1 获取药品列表

**接口信息**
- **URL**: `/drugs`
- **Method**: GET

**查询参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | string | 否 | 搜索关键词 |
| category | string | 否 | 药品分类 |
| type | string | 否 | 类型（western/chinese） |

**响应示例**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "drugId": "drug_001",
        "drugName": "氨氯地平片",
        "genericName": "苯磺酸氨氯地平",
        "specification": "5mg*28片",
        "manufacturer": "辉瑞制药",
        "category": "心血管系统用药",
        "type": "western",
        "price": 45.5,
        "stock": 100,
        "unit": "盒",
        "indications": "高血压、慢性稳定性心绞痛",
        "contraindications": "对二氢吡啶类药物过敏者禁用",
        "storage": "遮光，密封保存"
      }
    ],
    "pagination": {
      "page": 1,
      "size": 20,
      "total": 500
    }
  }
}
```

### 8.2 获取药品详情

**接口信息**
- **URL**: `/drugs/{drugId}`
- **Method**: GET

**响应示例**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "drugId": "drug_001",
    "drugName": "氨氯地平片",
    "genericName": "苯磺酸氨氯地平",
    "englishName": "Amlodipine Besylate Tablets",
    "specification": "5mg*28片",
    "manufacturer": "辉瑞制药",
    "approvalNo": "国药准字H20020390",
    "category": "心血管系统用药",
    "subCategory": "钙通道阻滞剂",
    "type": "western",
    "form": "片剂",
    "price": 45.5,
    "stock": 100,
    "unit": "盒",
    "indications": "1.高血压。2.慢性稳定性心绞痛及变异型心绞痛。",
    "contraindications": "对二氢吡啶类钙拮抗剂过敏者禁用。",
    "adverseReactions": "头痛、水肿、疲劳、失眠、恶心、腹痛...",
    "precautions": "1.肝功能受损患者慎用。2.肾功能衰竭患者可采用正常剂量。",
    "drugInteractions": "与西咪替丁、葡萄柚汁等合用可能增加血药浓度。",
    "dosage": "治疗高血压的初始剂量为5mg，每日一次。",
    "storage": "遮光，密封保存",
    "shelfLife": "36个月",
    "image": "https://..."
  }
}
```

### 8.3 库存管理

**接口信息**
- **URL**: `/drugs/inventory`
- **Method**: GET
- **权限**: pharmacist/admin

**查询参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| status | string | 否 | 状态（normal/low/out） |
| expiryWarning | boolean | 否 | 是否只显示临期药品 |

**响应示例**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "summary": {
      "totalDrugs": 500,
      "lowStock": 10,
      "outOfStock": 2,
      "expiringSoon": 5
    },
    "list": [
      {
        "drugId": "drug_001",
        "drugName": "氨氯地平片",
        "specification": "5mg*28片",
        "stock": 100,
        "safetyStock": 20,
        "status": "normal",
        "expiryDate": "2028-12-01",
        "daysToExpiry": 968
      }
    ],
    "pagination": {
      "page": 1,
      "size": 20,
      "total": 500
    }
  }
}
```

### 8.4 入库操作

**接口信息**
- **URL**: `/drugs/inventory/inbound`
- **Method**: POST
- **权限**: pharmacist

**请求参数**
```json
{
  "drugId": "drug_001",
  "batchNo": "LOT20260401",
  "quantity": 100,
  "unitPrice": 35.0,
  "expiryDate": "2028-12-01",
  "supplier": "医药公司A",
  "remark": "常规采购"
}
```

---

## 9. 慢病管理模块

### 9.1 获取慢病档案

**接口信息**
- **URL**: `/chronic-diseases`
- **Method**: GET

**响应示例**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "chronicId": "chr_001",
        "diseaseType": "hypertension",
        "diseaseName": "高血压",
        "diagnosisDate": "2020-05-01",
        "diagnosisHospital": "朝阳医院",
        "currentStatus": "控制中",
        "riskLevel": "medium",
        "targetValues": {
          "bloodPressureSystolic": 130,
          "bloodPressureDiastolic": 80
        },
        "managementDoctor": {
          "doctorId": "doc_001",
          "name": "王医生"
        },
        "followUpPlan": {
          "frequency": "每月一次",
          "nextDate": "2026-05-08"
        },
        "statistics": {
          "totalFollowUps": 24,
          "complianceRate": 95
        }
      }
    ]
  }
}
```

### 9.2 创建慢病档案

**接口信息**
- **URL**: `/chronic-diseases`
- **Method**: POST
- **权限**: doctor

**请求参数**
```json
{
  "patientId": "usr_1234567890",
  "diseaseType": "hypertension",
  "diseaseName": "高血压",
  "diagnosisDate": "2020-05-01",
  "diagnosisHospital": "朝阳医院",
  "currentStatus": "控制中",
  "riskLevel": "medium",
  "targetValues": {
    "bloodPressureSystolic": 130,
    "bloodPressureDiastolic": 80
  },
  "managementDoctorId": "doc_001"
}
```

### 9.3 获取随访计划

**接口信息**
- **URL**: `/chronic-diseases/{chronicId}/follow-ups`
- **Method**: GET

**响应示例**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "chronicId": "chr_001",
    "diseaseName": "高血压",
    "upcoming": [
      {
        "followUpId": "fu_001",
        "plannedDate": "2026-05-08",
        "type": "phone",
        "status": "pending",
        "content": "血压监测情况询问"
      }
    ],
    "history": [
      {
        "followUpId": "fu_000",
        "plannedDate": "2026-04-08",
        "actualDate": "2026-04-08",
        "type": "visit",
        "status": "completed",
        "result": "血压控制良好",
        "doctor": {
          "doctorId": "doc_001",
          "name": "王医生"
        }
      }
    ]
  }
}
```

### 9.4 记录随访结果

**接口信息**
- **URL**: `/chronic-diseases/follow-ups/{followUpId}/record`
- **Method**: POST
- **权限**: doctor

**请求参数**
```json
{
  "actualDate": "2026-04-08",
  "type": "visit",
  "content": "患者血压控制良好，继续当前用药方案",
  "indicators": {
    "bloodPressureSystolic": 130,
    "bloodPressureDiastolic": 82,
    "heartRate": 72
  },
  "compliance": "good",
  "adverseEvents": "无",
  "nextFollowUpDate": "2026-05-08",
  "remark": ""
}
```

### 9.5 健康监测数据

**接口信息**
- **URL**: `/chronic-diseases/{chronicId}/monitoring`
- **Method**: GET

**响应示例**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "chronicId": "chr_001",
    "diseaseName": "高血压",
    "trend": {
      "period": "3个月",
      "averageSystolic": 135,
      "averageDiastolic": 85,
      "trend": "stable"
    },
    "alerts": [
      {
        "alertId": "alt_001",
        "alertType": "high_blood_pressure",
        "value": "155/95",
        "alertTime": "2026-04-01T08:30:00Z",
        "status": "resolved",
        "handledBy": "doc_001"
      }
    ],
    "records": [
      {
        "recordId": "rec_001",
        "recordDate": "2026-04-08",
        "indicators": {
          "bloodPressureSystolic": 132,
          "bloodPressureDiastolic": 84,
          "heartRate": 70
        },
        "source": "manual"
      }
    ]
  }
}
```

---

## 10. 健康教育模块

### 10.1 获取健康资讯列表

**接口信息**
- **URL**: `/health-articles`
- **Method**: GET

**查询参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| category | string | 否 | 分类 |
| keyword | string | 否 | 搜索关键词 |
| isRecommended | boolean | 否 | 是否推荐 |

**响应示例**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "articleId": "art_001",
        "title": "高血压患者的日常饮食建议",
        "summary": "合理的饮食对于控制血压至关重要...",
        "coverImage": "https://...",
        "category": "慢病管理",
        "tags": ["高血压", "饮食"],
        "author": {
          "doctorId": "doc_001",
          "name": "王医生",
          "title": "主任医师"
        },
        "viewCount": 1523,
        "likeCount": 89,
        "isRecommended": true,
        "publishedAt": "2026-03-01T10:00:00Z"
      }
    ],
    "pagination": {
      "page": 1,
      "size": 10,
      "total": 50
    }
  }
}
```

### 10.2 获取资讯详情

**接口信息**
- **URL**: `/health-articles/{articleId}`
- **Method**: GET

**响应示例**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "articleId": "art_001",
    "title": "高血压患者的日常饮食建议",
    "content": "<html>...</html>",
    "coverImage": "https://...",
    "category": "慢病管理",
    "tags": ["高血压", "饮食"],
    "author": {
      "doctorId": "doc_001",
      "name": "王医生",
      "title": "主任医师",
      "avatar": "https://..."
    },
    "viewCount": 1523,
    "likeCount": 89,
    "isLiked": false,
    "isCollected": false,
    "publishedAt": "2026-03-01T10:00:00Z",
    "relatedArticles": [
      {
        "articleId": "art_002",
        "title": "如何正确测量血压"
      }
    ]
  }
}
```

### 10.3 点赞/收藏文章

**接口信息**
- **URL**: `/health-articles/{articleId}/like`
- **Method**: POST

**请求参数**
```json
{
  "action": "like"
}
```

### 10.4 获取健康课程列表

**接口信息**
- **URL**: `/health-courses`
- **Method**: GET

**响应示例**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "courseId": "course_001",
        "title": "高血压自我管理课程",
        "coverImage": "https://...",
        "description": "系统学习高血压的日常管理知识",
        "category": "慢病管理",
        "instructor": {
          "doctorId": "doc_001",
          "name": "王医生"
        },
        "lessonCount": 10,
        "duration": 120,
        "enrollmentCount": 500,
        "rating": 4.8,
        "progress": 30
      }
    ]
  }
}
```

### 10.5 发布文章（医生/管理员）

**接口信息**
- **URL**: `/health-articles`
- **Method**: POST
- **权限**: doctor/admin

**请求参数**
```json
{
  "title": "文章标题",
  "content": "<html>...</html>",
  "summary": "文章摘要",
  "coverImage": "https://...",
  "category": "慢病管理",
  "tags": ["高血压", "饮食"],
  "isRecommended": false
}
```

---

## 11. 数据统计模块

### 11.1 业务统计概览

**接口信息**
- **URL**: `/statistics/overview`
- **Method**: GET
- **权限**: admin

**查询参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| startDate | string | 是 | 开始日期 |
| endDate | string | 是 | 结束日期 |

**响应示例**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "period": {
      "startDate": "2026-04-01",
      "endDate": "2026-04-08"
    },
    "appointments": {
      "total": 500,
      "completed": 450,
      "cancelled": 30,
      "noShow": 20,
      "completionRate": 90
    },
    "consultations": {
      "total": 300,
      "text": 250,
      "video": 50,
      "completed": 280,
      "averageResponseTime": 15
    },
    "prescriptions": {
      "total": 400,
      "totalAmount": 50000
    },
    "newUsers": 100,
    "activeUsers": 800
  }
}
```

### 11.2 就诊统计

**接口信息**
- **URL**: `/statistics/visits`
- **Method**: GET
- **权限**: admin

**响应示例**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "trend": [
      {
        "date": "2026-04-01",
        "outpatient": 50,
        "inpatient": 5,
        "total": 55
      }
    ],
    "byDepartment": [
      {
        "departmentId": "dept_001",
        "departmentName": "内科",
        "count": 200,
        "percentage": 40
      }
    ],
    "byDoctor": [
      {
        "doctorId": "doc_001",
        "doctorName": "王医生",
        "count": 100,
        "percentage": 20
      }
    ]
  }
}
```

### 11.3 慢病统计

**接口信息**
- **URL**: `/statistics/chronic-diseases`
- **Method**: GET
- **权限**: admin

**响应示例**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "overview": {
      "totalPatients": 500,
      "hypertension": 300,
      "diabetes": 200,
      "both": 100
    },
    "controlRate": {
      "hypertension": {
        "good": 200,
        "fair": 80,
        "poor": 20
      },
      "diabetes": {
        "good": 150,
        "fair": 40,
        "poor": 10
      }
    },
    "followUpStatistics": {
      "totalPlanned": 400,
      "completed": 380,
      "completionRate": 95
    }
  }
}
```

### 11.4 用户统计

**接口信息**
- **URL**: `/statistics/users`
- **Method**: GET
- **权限**: admin

**响应示例**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalUsers": 5000,
    "newUsersToday": 10,
    "newUsersThisMonth": 100,
    "activeUsersToday": 800,
    "activeUsersThisMonth": 2000,
    "byAge": [
      {
        "range": "18-30",
        "count": 500,
        "percentage": 10
      },
      {
        "range": "60+",
        "count": 2000,
        "percentage": 40
      }
    ],
    "byGender": {
      "male": 2000,
      "female": 3000
    }
  }
}
```

---

## 12. 附录

### 12.1 枚举值定义

**用户角色（Role）**
| 值 | 说明 |
|----|------|
| resident | 居民/患者 |
| doctor | 医生 |
| nurse | 护士 |
| pharmacist | 药师 |
| admin | 管理员 |

**预约状态（AppointmentStatus）**
| 值 | 说明 |
|----|------|
| pending | 待确认 |
| confirmed | 已确认 |
| in_progress | 就诊中 |
| completed | 已完成 |
| cancelled | 已取消 |
| no_show | 爽约 |

**问诊状态（ConsultationStatus）**
| 值 | 说明 |
|----|------|
| pending | 待回复 |
| in_progress | 进行中 |
| completed | 已完成 |
| closed | 已关闭 |
| expired | 已过期 |

**处方状态（PrescriptionStatus）**
| 值 | 说明 |
|----|------|
| pending | 待审核 |
| approved | 已审核 |
| rejected | 已拒绝 |
| completed | 已完成 |
| expired | 已过期 |

**药品类型（DrugType）**
| 值 | 说明 |
|----|------|
| western | 西药 |
| chinese | 中成药 |
| herbal | 中草药 |

### 12.2 错误码列表

| 错误码 | 说明 |
|--------|------|
| 1001 | 手机号已注册 |
| 1002 | 手机号未注册 |
| 1003 | 密码错误 |
| 1004 | 验证码错误 |
| 1005 | 验证码已过期 |
| 1006 | 账号已锁定 |
| 1007 | 实名认证失败 |
| 2001 | 号源已满 |
| 2002 | 预约时间冲突 |
| 2003 | 超出预约次数限制 |
| 3001 | 医生不在线 |
| 3002 | 问诊已过期 |
| 4001 | 库存不足 |
| 4002 | 药品已下架 |
| 5001 | 无权限访问 |
| 5002 | 档案未授权 |

### 12.3 数据字典

**血压分级标准**
| 分级 | 收缩压(mmHg) | 舒张压(mmHg) |
|------|-------------|-------------|
| 正常 | <120 | <80 |
| 正常高值 | 120-139 | 80-89 |
| 1级高血压 | 140-159 | 90-99 |
| 2级高血压 | 160-179 | 100-109 |
| 3级高血压 | ≥180 | ≥110 |

**血糖标准**
| 指标 | 正常值 | 异常值 |
|------|--------|--------|
| 空腹血糖 | 3.9-6.1 mmol/L | ≥7.0 mmol/L |
| 餐后2小时血糖 | <7.8 mmol/L | ≥11.1 mmol/L |

---

**文档结束**
