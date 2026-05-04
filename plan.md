# 医生端 / 管理员后台 实施计划

## 概述
为社区医疗服务系统添加 **医生端** 和 **管理员后台** 功能，支持三种角色（患者/医生/管理员）的独立界面和权限。

---

## 第一步：修复后端编译错误（阻塞项）

### 问题
`ConsultationServiceImpl.java` 存在两个编译错误：
1. `consultationMapper` 变量未定义（应使用继承的 `baseMapper` 或注入 `ConsultationMapper`）
2. `BusinessException` 类在项目中不存在

### 修复方案
- 在 `common` 包下创建 `BusinessException.java`（RuntimeException 子类）
- 在 `ConsultationServiceImpl` 中注入 `ConsultationMapper` 或用 `baseMapper` 替换 `consultationMapper` 调用
- 确认 `StatisticsController.java` 中的 `IService<SysUser>` 注入方式正确

### 涉及文件
- `demo/.../common/BusinessException.java` （新建）
- `demo/.../service/impl/ConsultationServiceImpl.java` （修复）
- `demo/.../controller/StatisticsController.java` （检查）

---

## 第二步：后端 - 补充医生端/管理员端 API

### 2.1 AppointmentService 扩展
- 添加 `getAppointmentsByDoctorId(Long doctorId, String date)` 方法用于医生查看指定日期排班
- AppointmentController 的 `/doctor/today` 和 `/doctor/schedule` 当前实现有 bug（用 userId 查而非 doctorId 关联），需要修复

### 2.2 用户管理 API（管理员）
- 已有 `UserController`，确认管理员可以查询/修改用户角色

### 2.3 科室管理 API（管理员）
- 已有 `/api/appointment/departments` 查询接口
- 需要添加科室 CRUD（创建/更新/删除）接口给管理员使用
- 创建 `DepartmentController.java` 或在 `AppointmentController` 中添加管理端点

### 涉及文件
- `demo/.../service/AppointmentService.java` （扩展）
- `demo/.../service/impl/AppointmentServiceImpl.java` （实现）
- `demo/.../controller/AppointmentController.java` （修复医生端 bug）
- `demo/.../controller/DepartmentController.java` （新建，科室 CRUD）

---

## 第三步：前端 - 创建管理员页面

### 3.1 AdminDepartments.vue - 科室管理
- 表格展示科室列表（名称、描述、医生数）
- 新增/编辑/删除科室的对话框

### 3.2 AdminDoctors.vue - 医生管理
- 表格展示医生列表（姓名、职称、科室、状态）
- 新增/编辑医生信息
- 绑定/解绑医生账号

### 3.3 AdminStatistics.vue - 数据统计
- 统计概览卡片（用户数、医生数、预约数、问诊数）
- 使用 `GET /api/statistics/overview` 获取数据

### 3.4 API 文件
- `src/api/admin.ts` - 管理员相关接口（科室 CRUD、用户管理、统计数据）

### 涉及文件
- `study/src/views/AdminDepartments.vue` （新建）
- `study/src/views/AdminDoctors.vue` （新建）
- `study/src/views/AdminStatistics.vue` （新建）
- `study/src/api/admin.ts` （新建）

---

## 第四步：前端 - 创建医生端页面

### 4.1 DoctorSchedule.vue - 今日排班
- 展示今日预约患者列表
- 查看患者基本信息和症状描述
- 标记已接诊/未到

### 4.2 DoctorConsultations.vue - 处理问诊
- 展示待处理问诊列表
- 回复问诊（填写诊断意见、医生回复）
- 标记问诊完成

### 4.3 DoctorPrescriptions.vue - 开具处方
- 从问诊记录创建处方
- 选择药品、用量、用法
- 查看历史处方

### 4.4 API 文件
- `src/api/doctor.ts` - 医生相关接口（排班查询、问诊管理、处方开具）

### 涉及文件
- `study/src/views/DoctorSchedule.vue` （新建）
- `study/src/views/DoctorConsultations.vue` （新建）
- `study/src/views/DoctorPrescriptions.vue` （新建）
- `study/src/api/doctor.ts` （新建）

---

## 第五步：前端 - 更新路由和 Layout

### 5.1 路由更新 `router/index.ts`
- 添加管理员路由：`/admin/departments`, `/admin/doctors`, `/admin/statistics`
- 添加医生路由：`/doctor/schedule`, `/doctor/consultations`, `/doctor/prescriptions`
- 添加路由守卫：根据角色限制可访问页面

### 5.2 Layout.vue 更新
- 三角色菜单：
  - **管理员**：数据统计、科室管理、医生管理、用户管理、药品管理、预约管理、问诊管理、处方管理、健康档案
  - **医生**：今日排班、处理问诊、开具处方、个人中心
  - **患者**：工作台、预约挂号、在线问诊、我的处方、健康档案、个人中心

### 5.3 user store 更新
- 添加 `isDoctor` 计算属性
- 添加角色判断工具方法

### 涉及文件
- `study/src/router/index.ts` （修改）
- `study/src/views/Layout.vue` （修改）
- `study/src/stores/user.ts` （修改）

---

## 第六步：构建验证

- 后端：`mvn compile` 确保无编译错误
- 前端：`npm run build` 确保无类型错误
- 功能验证：确认三种角色菜单正确显示

---

## 优先级顺序
1. 修复后端编译错误（阻塞项）
2. 后端 API 补充
3. 前端管理员页面
4. 前端医生端页面
5. 路由和 Layout 更新
6. 构建验证
