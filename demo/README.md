# 用户生成器系统

基于Spring Boot + MyBatis-Plus的用户管理系统，支持用户注册、登录、角色管理等功能。

## 技术栈

- Spring Boot 2.6.13
- MyBatis-Plus 3.5.3.1
- MySQL 8.0+
- Spring Security
- MapStruct 1.5.3.Final
- Hutool 5.8.16
- Lombok 1.18.24
- JUnit5 + Mockito

## 项目结构

```
src/main/java/com/example/usergenerator/
├── annotation/          # 自定义注解
├── common/             # 通用类
├── config/             # 配置类
├── constant/           # 常量定义
├── controller/         # 控制层
├── converter/          # 对象转换器
├── dto/                # 数据传输对象
├── entity/             # 实体类
├── exception/          # 异常处理
├── mapper/             # 持久层
├── service/            # 服务层
├── util/               # 工具类
└── UserGeneratorApplication.java
```

## 快速开始

### 1. 环境要求

- JDK 11+
- Maven 3.6+
- MySQL 8.0+
- Redis（可选，用于防重复提交）

### 2. 数据库初始化

执行数据库脚本：

```bash
mysql -u root -p < src/main/resources/schema.sql
```

### 3. 配置修改

修改 `src/main/resources/application-dev.yml` 中的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/user_generator_dev?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

### 4. 启动项目

```bash
mvn clean install
mvn spring-boot:run
```

### 5. 访问接口

项目启动后，访问：http://localhost:8080

## API文档

### 用户管理

#### 1. 生成用户

```http
POST /api/user/generate
```

**响应示例：**
```json
{
  "code": 200,
  "msg": "用户生成成功",
  "data": {
    "username": "user_abc123",
    "password": "Test1234",
    "role": "user"
  }
}
```

#### 2. 用户登录

```http
POST /api/user/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "Test1234"
}
```

**响应示例：**
```json
{
  "code": 200,
  "msg": "登录成功",
  "data": {
    "token": "TOKEN_1234567890",
    "role": "user",
    "user": {
      "id": 1,
      "username": "testuser",
      "role": "user",
      "createTime": "2024-01-01T10:00:00"
    }
  }
}
```

#### 3. 用户注册

```http
POST /api/user/register
Content-Type: application/json
X-User-Role: user

{
  "username": "newuser",
  "password": "Test1234",
  "role": "user"
}
```

#### 4. 获取用户列表（管理员）

```http
GET /api/user/list
X-User-Role: admin
```

#### 5. 删除用户（管理员）

```http
DELETE /api/user/delete/{id}
X-User-Role: admin
```

#### 6. 更新用户角色（管理员）

```http
PUT /api/user/role
Content-Type: application/json
X-User-Role: admin

{
  "id": 1,
  "role": "admin"
}
```

### 记录管理

#### 1. 创建记录

```http
POST /api/record/add
Content-Type: application/json
X-User-Role: user

{
  "category": "餐饮",
  "type": "支出",
  "status": "已完成",
  "amount": 100.00
}
```

#### 2. 更新记录

```http
PUT /api/record/update/{id}
Content-Type: application/json
X-User-Role: user

{
  "category": "交通",
  "amount": 50.00
}
```

#### 3. 删除记录

```http
DELETE /api/record/delete/{id}
X-User-Role: user
```

#### 4. 批量删除记录

```http
DELETE /api/record/delete/batch
Content-Type: application/json
X-User-Role: user

[1, 2, 3]
```

#### 5. 分页查询记录

```http
GET /api/record/list?page=1&size=10&type=支出&category=餐饮
X-User-Role: user
```

#### 6. 初始化测试数据

```http
GET /api/record/init
X-User-Role: user
```

## 测试

### 运行单元测试

```bash
mvn test
```

### 运行特定测试类

```bash
mvn test -Dtest=SysUserServiceTest
```

## 配置说明

### 多环境配置

项目支持多环境配置，通过修改 `spring.profiles.active` 切换环境：

- `dev` - 开发环境
- `test` - 测试环境
- `prod` - 生产环境

### 日志配置

日志级别可在 `application-{profile}.yml` 中配置：

```yaml
logging:
  level:
    root: INFO
    com.example.usergenerator: DEBUG
    com.baomidou.mybatisplus: DEBUG
```

## 重构说明

本项目经过全面重构，详细的重构说明请查看 [REFACTORING.md](REFACTORING.md)

### 主要重构点

1. **分层架构优化**：Controller/Service/Repository/DTO/VO/Entity
2. **统一返回结果封装**：Result<T> + ResultCode
3. **全局异常处理**：GlobalExceptionHandler
4. **DTO/VO层**：使用MapStruct进行对象转换
5. **参数校验**：使用Validation注解
6. **权限控制**：AOP切面 + 自定义注解
7. **防重复提交**：Redis + AOP
8. **XSS过滤**：Filter实现
9. **多环境配置**：dev/test/prod
10. **单元测试**：JUnit5 + Mockito

## 安全措施

1. **密码加密**：使用BCrypt加密存储
2. **XSS过滤**：自动过滤恶意脚本
3. **防重复提交**：Redis实现分布式锁
4. **参数校验**：Validation注解校验
5. **权限控制**：基于角色的访问控制

## 开发规范

### 代码风格

- 遵循阿里巴巴Java开发手册
- 使用4空格缩进
- UTF-8编码
- 使用Lombok简化代码

### 命名规范

- 类名：大驼峰命名法
- 方法名：小驼峰命名法
- 常量：全大写下划线分隔
- 包名：全小写

### 注释规范

- 类注释：说明类的职责
- 方法注释：说明方法的功能、参数、返回值
- 复杂逻辑：添加行内注释

## 常见问题

### 1. 数据库连接失败

检查数据库配置是否正确，确保MySQL服务已启动。

### 2. 防重复提交功能不生效

确保Redis服务已启动，并检查Redis配置。

### 3. 单元测试失败

检查Mock配置是否正确，确保依赖注入正常。

## 贡献指南

1. Fork本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启Pull Request

## 许可证

本项目采用 MIT 许可证。

## 联系方式

如有问题，请提交Issue或联系项目维护者。
