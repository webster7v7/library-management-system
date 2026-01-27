# 图书馆管理系统 - 生产环境部署指南

## 架构说明

本配置将所有服务容器化，包括：
- **MySQL 8.0**：数据库服务（不暴露到宿主机）
- **Redis 7**：缓存和会话管理（不暴露到宿主机）
- **Backend**：Spring Boot 后端服务（端口 8082）
- **Frontend**：Nginx 前端服务（端口 82）

所有服务在 Docker 内部网络中通信，仅 Backend 和 Frontend 暴露到宿主机。

## 部署步骤

### 1. 准备环境

确保云服务器已安装：
- Docker (20.10+)
- Docker Compose 插件

```bash
# 检查 Docker 版本
docker --version

# 检查 Docker Compose
docker compose version
```

### 2. 上传项目文件

将整个项目上传到云服务器：
```bash
# 假设项目在 /home/webster/library_management_system
cd /home/webster/library_management_system
```

### 3. 检查配置文件

确认 `.env` 文件存在且配置正确：
```bash
cat .env
```

### 4. 进入 docker 目录
```bash
cd docker
```

### 5. 停止旧容器（如果存在）
```bash
docker compose -f docker-compose.prod.yml down
```

### 6. 启动所有服务
```bash
docker compose -f docker-compose.prod.yml up -d --build
```

### 7. 查看服务状态
```bash
docker compose -f docker-compose.prod.yml ps
```

### 8. 查看日志
```bash
# 查看所有服务日志
docker compose -f docker-compose.prod.yml logs -f

# 查看特定服务日志
docker compose -f docker-compose.prod.yml logs -f backend
docker compose -f docker-compose.prod.yml logs -f mysql
```

## 验证部署

### 1. 检查容器健康状态
```bash
docker compose -f docker-compose.prod.yml ps
```

所有服务应该显示为 `Up (healthy)` 或 `Up`。

### 2. 测试后端健康检查
```bash
curl http://localhost:8082/api/health
```

应该返回 `{"status":"UP"}` 或类似的健康状态。

### 3. 测试前端访问
```bash
curl http://localhost:82/
```

应该返回 HTML 内容。

### 4. 测试数据库连接
```bash
docker exec library-backend mysql -h mysql -u root -pmysql_ZAxX6G -e "SELECT 1;"
```

### 5. 测试 Redis 连接
```bash
docker exec library-backend redis-cli -h redis -a redis_AG2sFZ ping
```

## 访问应用

- **前端地址**: http://your-server-ip:82
- **后端 API**: http://your-server-ip:8082
- **API 文档**: http://your-server-ip:8082/swagger-ui.html

**默认账号：**
- 管理员: admin / admin
- 普通用户: user / user

## 常用管理命令

### 查看日志
```bash
# 所有服务
docker compose -f docker-compose.prod.yml logs -f

# 特定服务
docker compose -f docker-compose.prod.yml logs backend
docker compose -f docker-compose.prod.yml logs frontend
docker compose -f docker-compose.prod.yml logs mysql
docker compose -f docker-compose.prod.yml logs redis
```

### 重启服务
```bash
# 重启所有服务
docker compose -f docker-compose.prod.yml restart

# 重启特定服务
docker compose -f docker-compose.prod.yml restart backend
```

### 停止服务
```bash
docker compose -f docker-compose.prod.yml down
```

### 停止并删除数据卷（谨慎使用）
```bash
docker compose -f docker-compose.prod.yml down -v
```

### 进入容器
```bash
# 进入后端容器
docker exec -it library-backend sh

# 进入 MySQL 容器
docker exec -it library-mysql bash

# 进入 Redis 容器
docker exec -it library-redis sh

# 进入前端容器
docker exec -it library-frontend sh
```

### 查看资源使用
```bash
docker stats
```

## 数据备份

### 备份 MySQL 数据
```bash
docker exec library-mysql mysqldump -u root -pmysql_ZAxX6G library_management > backup.sql
```

### 恢复 MySQL 数据
```bash
docker exec -i library-mysql mysql -u root -pmysql_ZAxX6G library_management < backup.sql
```

### 备份 Redis 数据
```bash
docker exec library-redis redis-cli -a redis_AG2sFZ BGSAVE
```

## 故障排查

### 1. 容器无法启动
```bash
# 查看容器日志
docker compose -f docker-compose.prod.yml logs

# 检查容器状态
docker compose -f docker-compose.prod.yml ps -a
```

### 2. 数据库连接失败
```bash
# 检查 MySQL 容器状态
docker compose -f docker-compose.prod.yml logs mysql

# 进入 MySQL 容器测试连接
docker exec -it library-mysql mysql -u root -pmysql_ZAxX6G
```

### 3. Redis 连接失败
```bash
# 检查 Redis 容器状态
docker compose -f docker-compose.prod.yml logs redis

# 进入 Redis 容器测试连接
docker exec -it library-redis redis-cli -a redis_AG2sFZ
```

### 4. 前端无法访问
```bash
# 检查 Nginx 配置
docker exec library-frontend cat /etc/nginx/nginx.conf

# 检查前端日志
docker compose -f docker-compose.prod.yml logs frontend
```

### 5. 端口冲突
如果 8082 或 82 端口被占用，修改 `docker-compose.prod.yml` 中的端口映射：
```yaml
backend:
  ports:
    - "新端口:8080"

frontend:
  ports:
    - "新端口:80"
```

## 安全建议

1. **修改默认密码**：立即修改 `.env` 文件中的数据库和 Redis 密码
2. **配置防火墙**：只开放必要的端口（82）
3. **启用 HTTPS**：使用 Nginx 反向代理配置 SSL/TLS
4. **定期备份**：设置定时任务自动备份数据库
5. **监控日志**：定期检查应用和系统日志

## 性能优化

1. **调整 MySQL 配置**：根据服务器资源优化 `my.cnf`
2. **配置 Redis 持久化**：根据需求选择 RDB 或 AOF
3. **启用 Nginx 缓存**：配置静态资源缓存
4. **调整 JVM 参数**：在 Dockerfile 中优化 Java 内存设置

## 更新应用

```bash
# 1. 拉取最新代码
git pull

# 2. 重新构建并启动
cd docker
docker compose -f docker-compose.prod.yml up -d --build

# 3. 查看日志确认更新成功
docker compose -f docker-compose.prod.yml logs -f
```

## 卸载

```bash
# 停止并删除容器
docker compose -f docker-compose.prod.yml down

# 删除数据卷（会删除所有数据）
docker compose -f docker-compose.prod.yml down -v

# 删除镜像
docker rmi library-backend library-frontend
```
