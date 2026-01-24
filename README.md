# 图书馆管理系统

基于 Spring Boot + Vue 3 + MySQL + Redis 的全栈图书馆管理系统。

## 技术栈

### 后端
- Spring Boot 3.2.0
- MyBatis Plus 3.5.5
- MySQL 8.0
- Redis 7
- JWT + Spring Security
- Swagger

### 前端
- Vue 3
- TypeScript
- Vite
- Tailwind CSS
- Axios
- Pinia
- Vue Router

## 项目结构

```
library-management-system/
├── backend/              # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/library/
│   │   │   │   ├── controller/   # 控制器
│   │   │   │   ├── service/      # 服务层
│   │   │   │   ├── mapper/       # 数据访问层
│   │   │   │   ├── entity/       # 实体类
│   │   │   │   ├── config/       # 配置类
│   │   │   │   ├── security/     # 安全配置
│   │   │   │   └── util/         # 工具类
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       └── schema.sql
│   │   └── test/
│   └── pom.xml
├── frontend/             # 前端项目
│   ├── src/
│   │   ├── views/        # 页面组件
│   │   ├── layouts/      # 布局组件
│   │   ├── stores/       # 状态管理
│   │   ├── api/          # API接口
│   │   ├── utils/        # 工具函数
│   │   └── router/       # 路由配置
│   ├── package.json
│   └── vite.config.ts
├── docker/               # Docker配置
│   └── docker-compose.yml
└── docs/                 # 项目文档
```

## 功能特性

### 用户功能
- 用户登录/注册
- 查看图书列表
- 借阅图书
- 归还图书
- 续借图书
- 查看借阅历史

### 管理员功能
- 图书管理（增删改查）
- 用户管理
- 借阅记录管理
- 系统统计

## 快速开始

### 前置要求
- JDK 17+
- Node.js 20+
- MySQL 8.0+
- Redis 7+
- Maven 3.9+
- pnpm

### 后端启动

1. 创建数据库并执行SQL脚本
```bash
mysql -u root -p < backend/src/main/resources/schema.sql
```

2. 修改配置文件
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/library_management
    username: root
    password: your_password
  redis:
    host: localhost
    port: 6379
```

3. 启动后端服务
```bash
cd backend
mvn spring-boot:run
```

后端服务将在 http://localhost:8080 启动

API文档访问: http://localhost:8080/swagger-ui.html

### 前端启动

1. 安装依赖
```bash
cd frontend
pnpm install
```

2. 启动开发服务器
```bash
pnpm dev
```

前端服务将在 http://localhost:5173 启动

### Docker部署

1. 构建并启动所有服务
```bash
cd docker
docker-compose up -d
```

2. 访问应用
- 前端: http://localhost
- 后端: http://localhost:8080
- API文档: http://localhost:8080/swagger-ui.html

3. 停止服务
```bash
docker-compose down
```

## 默认账号

- 管理员: admin / admin
- 普通用户: user / user

## 开发说明

### 后端开发
- 使用MyBatis Plus简化数据库操作
- JWT实现无状态认证
- Redis缓存热点数据
- Swagger自动生成API文档

### 前端开发
- Vue 3 Composition API
- TypeScript类型安全
- Tailwind CSS快速样式开发
- Pinia状态管理
- Vue Router路由管理

## 许可证

MIT License
