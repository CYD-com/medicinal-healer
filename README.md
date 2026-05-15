# 社区医疗服务系统

> 基于 Spring Boot + Vue 3 的前后端分离医疗管理系统

## 🛠️ 技术栈

### 后端（demo/）
- **Java** 11
- **Spring Boot** 2.6.13
- **Spring Security** + JWT 认证
- **MyBatis-Plus** 3.5.3.1（ORM）
- **MySQL** 8.0+ / **H2**（开发环境零依赖）
- **Redis**（验证码缓存、Token 黑名单）

### 前端（study/）
- **Vue 3** + **TypeScript**
- **Vite** 7（构建工具）
- **Element Plus**（UI 组件库）
- **Pinia**（状态管理）
- **Vue Router** 4（路由）
- **Axios**（HTTP 请求）

### 小程序（miniprogram/）
- 微信小程序原生框架
- 云开发能力支持

## 🚀 快速开始

### 方式一：H2 内存数据库（零依赖，推荐开发）

```bash
# 1. 启动后端（自动创建内存数据库和测试数据）
cd demo
mvn clean package -DskipTests
java -jar target/demo-0.0.1-SNAPSHOT.jar --spring.profiles.active=h2

# 2. 启动前端（另开一个终端）
cd study
npm install
npm run dev
```

### 方式二：MySQL + Redis（生产环境）

```bash
# 1. 启动 MySQL 和 Redis
# MySQL: localhost:3306, 数据库名: liangxi
# Redis: localhost:6379

# 2. 初始化数据库
mysql -u root -p < sql/drug_management_tables.sql

# 3. 启动后端
cd demo
mvn clean package -DskipTests
java -jar target/demo-0.0.1-SNAPSHOT.jar

# 4. 启动前端
cd study
npm install
npm run build
# 将 dist 目录部署到 Nginx
```

### 访问地址
| 服务 | 地址 |
|------|------|
| 前端管理后台 | http://localhost:5173 |
| 后端 API | http://localhost:8080 |
| H2 控制台 | http://localhost:8080/h2-console |

## 🔐 测试账号

| 用户名 | 密码 | 角色 | 权限说明 |
|--------|------|------|----------|
| `admin` | `123456` | 管理员 | 完整系统管理权限 |
| `doctor_zs` | `123456` | 医生 | 问诊、处方管理 |
| `doctor_ls` | `123456` | 医生 | 问诊、处方管理 |
| `user_w5` | `123456` | 患者 | 预约挂号、在线问诊 |

## 📁 项目结构

```
├── demo/                    # Spring Boot 后端
│   ├── src/main/java/com/example/usergenerator/
│   │   ├── controller/      # REST API 控制层
│   │   ├── service/         # 业务逻辑层
│   │   ├── mapper/          # MyBatis 数据访问层
│   │   ├── entity/          # 数据库实体类
│   │   ├── dto/             # 请求/响应数据传输对象
│   │   ├── config/          # 配置类（Security、Swagger 等）
│   │   └── util/            # 工具类
│   ├── src/main/resources/
│   │   ├── application.yml          # 默认配置
│   │   ├── application-h2.yml       # H2 内存数据库配置
│   │   └── application-prod.yml     # 生产环境配置
│   └── pom.xml              # Maven 依赖管理

├── study/                   # Vue 3 前端
│   ├── src/
│   │   ├── views/           # 页面组件
│   │   ├── components/      # 公共组件
│   │   ├── stores/          # Pinia 状态管理
│   │   ├── api/             # API 请求封装
│   │   ├── router/          # 路由配置
│   │   ├── utils/           # 工具函数（请求拦截器等）
│   │   ├── types/           # TypeScript 类型定义
│   │   └── composables/     # 组合式函数
│   ├── .env.development     # 开发环境变量
│   ├── .env.production      # 生产环境变量
│   ├── vite.config.ts       # Vite 配置
│   └── package.json         # 前端依赖

├── miniprogram/             # 微信小程序
│   ├── pages/               # 小程序页面
│   ├── components/          # 小程序组件
│   ├── utils/               # 工具函数
│   └── app.json             # 小程序配置

└── sql/                     # 数据库初始化脚本
    └── drug_management_tables.sql
```

## ✨ 功能模块

### 👤 用户管理
- 用户注册、登录、注销
- 角色权限控制（管理员/医生/患者）
- 用户信息管理

### 🏥 预约挂号
- 医生排班管理
- 在线预约挂号
- 预约记录查询

### 💬 在线问诊
- 图文问诊功能
- 问诊记录管理
- 智能问诊助手

### 📝 处方管理
- 电子处方开具
- 处方审核流程
- 药品库存管理

### 📊 数据统计
- 预约数据统计
- 就诊数据分析
- 系统运行监控

## 🔧 开发指南

### 环境要求
- Java 11+
- Node.js 18+
- Maven 3.8+

### 代码规范
- 后端：阿里巴巴 Java 开发手册
- 前端：ESLint + Prettier

### 提交规范
```
feat: 添加新功能
fix: 修复 bug
docs: 更新文档
refactor: 代码重构
style: 代码格式调整
test: 添加测试
```

## 📄 许可证

MIT License

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

---

**项目地址**：https://gitee.com/cydcom/medicinal-healer
