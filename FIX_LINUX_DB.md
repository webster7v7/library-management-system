# Ubuntu 云服务器数据库连接修复指南

## 问题描述

本地 Windows 环境正常，但部署到 Ubuntu 云服务器后，登录报错：
```
查询用户失败: 数据库异常
```

## 根因分析

`host.docker.internal` 在 Linux Docker 中默认不存在，需要通过 `extra_hosts` 配置。虽然你的配置文件已经正确配置，但还可能是以下问题：

---

## 修复步骤

### 步骤 1：运行诊断脚本

将 `diagnose-db.sh` 上传到服务器并执行：

```bash
chmod +x diagnose-db.sh
./diagnose-db.sh
```

根据诊断结果选择对应的修复方案。

---

### 步骤 2：修复 MySQL 监听地址（最常见问题）

**问题**：MySQL 默认只监听 `127.0.0.1`，容器无法访问。

**修复方法**：

```bash
# 1. 编辑 MySQL 配置
sudo vim /etc/mysql/mysql.conf.d/mysqld.cnf

# 2. 找到并修改 bind-address
bind-address = 0.0.0.0  # 改为 0.0.0.0

# 3. 重启 MySQL
sudo systemctl restart mysql

# 4. 验证修改
sudo netstat -tlnp | grep 3306
# 应该看到: 0.0.0.0:3306
```

---

### 步骤 3：配置防火墙（如果使用了 UFW）

**问题**：UFW 防火墙可能阻止了 Docker 网络的连接。

**修复方法**：

```bash
# 允许来自 Docker 网络的连接
sudo ufw allow from 172.17.0.0/16

# 或者开放 3306 端口（不推荐生产环境，除非必要）
sudo ufw allow 3306

# 查看防火墙状态
sudo ufw status
```

---

### 步骤 4：检查 MySQL 用户权限

**问题**：MySQL 用户可能不允许从 Docker 容器网关 IP 连接。

**修复方法**：

```bash
# 1. 登录 MySQL
mysql -u root -p

# 2. 检查用户权限
SELECT user, host FROM mysql.user WHERE user = 'root';

# 3. 如果 root 只允许 localhost，创建允许 Docker 连接的用户
CREATE USER 'library_user'@'172.17.0.1' IDENTIFIED BY 'your_password';

# 4. 授权
GRANT ALL PRIVILEGES ON library_management.* TO 'library_user'@'172.17.0.1';

# 5. 刷新权限
FLUSH PRIVILEGES;

# 6. 退出
exit;
```

然后更新环境变量中的数据库用户名和密码。

---

### 步骤 5：重新部署应用

完成上述修复后，重新部署：

```bash
cd /path/to/library_management_system/docker

# 停止旧容器
docker-compose down

# 重新构建并启动
docker-compose up -d --build

# 查看后端日志
docker-compose logs -f backend
```

---

## 验证修复

### 方法 1：查看日志

```bash
docker-compose logs backend | grep -i mysql
docker-compose logs backend | grep -i error
```

应该看到数据库连接成功的日志。

### 方法 2：访问登录接口

```bash
curl -X POST http://your-server-ip:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}'
```

应该返回成功的 JSON 响应。

### 方法 3：直接测试容器内连接

```bash
# 进入后端容器
docker exec -it library-backend sh

# 测试数据库连接
telnet host.docker.internal 3306
# 或
nc -zv host.docker.internal 3306
```

---

## 常见问题

### Q1: 修改了 bind-address 还是无法连接？

**A**: 检查 MySQL 服务是否真的重启成功：

```bash
sudo systemctl status mysql
sudo netstat -tlnp | grep 3306
```

### Q2: 防火墙已配置，但还是连接失败？

**A**: 检查云服务器提供商的安全组规则：

- 阿里云/腾讯云/华为云需要在控制台配置安全组
- 需要允许入站流量到 3306 端口（或者至少允许来自 Docker 网段的流量）

### Q3: `host.docker.internal` 无法解析？

**A**: 检查 Docker 版本和配置：

```bash
docker --version  # 需要 20.10+
docker-compose --version

# 验证 extra_hosts 生效
docker exec library-backend cat /etc/hosts | grep host.docker.internal
# 应该看到: 172.17.0.1    host.docker.internal
```

如果 `extra_hosts` 没有生效，尝试手动添加到 `/etc/hosts`：

```bash
# 获取网关 IP
GATEWAY_IP=$(docker network inspect bridge | grep -oP '"Gateway": "\K[^"]*' | head -1)

# 在容器内添加
docker exec library-backend sh -c "echo '$GATEWAY_IP host.docker.internal' >> /etc/hosts"
```

### Q4: 使用 Docker Compose 的 `.env` 文件配置？

**A**: 创建 `.env` 文件：

```bash
cd docker
cat > .env << EOF
SPRING_DATASOURCE_USERNAME=your_mysql_user
SPRING_DATASOURCE_PASSWORD=your_mysql_password
SPRING_REDIS_PASSWORD=your_redis_password
JWT_SECRET=your_jwt_secret
EOF
```

---

## 备选方案：容器化 MySQL

如果以上方案仍有问题，可以考虑将 MySQL 也容器化，避免宿主机连接问题：

```bash
cd docker
docker-compose -f ../docker-compose.yml up -d
```

这样所有服务都在 Docker 网络内部，网络配置更简单。

---

## 联系支持

如果以上方案都无法解决问题，请提供以下信息：

1. `diagnose-db.sh` 的完整输出
2. `docker-compose logs backend` 的最后 50 行
3. `sudo netstat -tlnp | grep 3306` 的输出
4. MySQL 版本：`mysql --version`
5. Docker 版本：`docker --version`
