# 云服务器部署指南

## 📋 目录
- [云服务器环境要求](#云服务器环境要求)
- [部署步骤](#部署步骤)
- [验证部署](#验证部署)
- [常用运维命令](#常用运维命令)
- [故障排查](#故障排查)

---

## ☁️ 云服务器环境要求

### 必装环境（仅需安装Docker）

云服务器**只需要安装Docker**，其他依赖都通过Docker容器提供。

#### 安装Docker

**Ubuntu/Debian:**
```bash
# 更新包索引
sudo apt update

# 安装必要依赖
sudo apt install -y apt-transport-https ca-certificates curl gnupg lsb-release

# 添加Docker官方GPG密钥
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

# 添加Docker仓库
echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# 安装Docker
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io

# 启动Docker服务
sudo systemctl start docker
sudo systemctl enable docker

# 验证安装
docker --version
```

**CentOS/RHEL:**
```bash
# 安装必要依赖
sudo yum install -y yum-utils

# 添加Docker仓库
sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo

# 安装Docker
sudo yum install -y docker-ce docker-ce-cli containerd.io

# 启动Docker服务
sudo systemctl start docker
sudo systemctl enable docker

# 验证安装
docker --version
```

#### 安装Docker Compose

```bash
# 下载Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose

# 添加执行权限
sudo chmod +x /usr/local/bin/docker-compose

# 创建软链接
sudo ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose

# 验证安装
docker-compose --version
```

#### 配置防火墙（如果启用）

```bash
# 开放必要端口
sudo ufw allow 80/tcp    # 前端HTTP
sudo ufw allow 443/tcp   # HTTPS（可选）
sudo ufw allow 22/tcp    # SSH

# 重启防火墙
sudo ufw reload
```

---

## 🚀 部署步骤

### 1. 上传项目文件到云服务器

**方法一：使用SCP上传**
```bash
# 在本地Windows PowerShell或Git Bash中执行
scp -r "D:\programming\project\library management system" root@your-server-ip:/root/library-management-system

# 或者使用WinSCP等图形化工具
```

**方法二：使用Git克隆（推荐）**
```bash
# 在云服务器上执行
cd /root
git clone <your-git-repository-url> library-management-system
cd library-management-system
```

### 2. 修改配置（重要！）

**安全警告：** 立即修改默认密码！

编辑 `docker-compose.yml`：
```bash
vim docker-compose.yml
```

修改以下密码：
```yaml
environment:
  MYSQL_ROOT_PASSWORD: your_secure_password_here  # 改为强密码
```

### 3. 构建并启动所有服务

```bash
# 进入项目目录
cd /root/library-management-system

# 构建并启动所有服务（首次运行）
docker-compose up -d --build

# 查看日志
docker-compose logs -f

# 或查看特定服务日志
docker-compose logs -f backend
docker-compose logs -f mysql
```

### 4. 等待服务启动

首次启动需要3-5分钟，因为需要：
- 下载并构建Docker镜像
- 初始化MySQL数据库
- 执行数据库脚本

查看所有容器状态：
```bash
docker-compose ps
```

确保所有服务都显示 `Up` 状态。

---

## ✅ 验证部署

### 1. 检查容器状态

```bash
# 查看所有容器
docker ps

# 查看容器详细信息
docker-compose ps
```

预期输出：
```
NAME                  STATUS
library-backend       Up (healthy)
library-frontend      Up
library-mysql         Up (healthy)
library-redis         Up (healthy)
```

### 2. 测试后端API

```bash
# 测试健康检查接口
curl http://localhost:8080/api/health

# 测试登录（使用admin账号）
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}'
```

预期响应：
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "id": 1,
    "username": "admin",
    "role": "ADMIN"
  }
}
```

### 3. 测试前端

在浏览器中访问：
- **前端:** `http://your-server-ip`
- **后端API文档:** `http://your-server-ip:8080/swagger-ui.html`

使用以下账号登录：
- 管理员: admin / admin
- 普通用户: user / user

### 4. 检查数据库连接

```bash
# 进入MySQL容器
docker exec -it library-mysql mysql -uroot -p

# 输入密码（docker-compose.yml中的MYSQL_ROOT_PASSWORD）

# 查看数据库
USE library_management;
SHOW TABLES;

# 查看用户
SELECT id, username, real_name, role FROM sys_user;

# 退出
exit;
```

---

## 🔧 常用运维命令

### 服务管理

```bash
# 启动所有服务
docker-compose up -d

# 停止所有服务
docker-compose down

# 停止并删除数据卷（谨慎使用！）
docker-compose down -v

# 重启特定服务
docker-compose restart backend
docker-compose restart frontend

# 重启所有服务
docker-compose restart
```

### 查看日志

```bash
# 查看所有服务日志
docker-compose logs

# 实时查看日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs backend
docker-compose logs mysql

# 查看最近100行日志
docker-compose logs --tail=100 backend
```

### 进入容器

```bash
# 进入后端容器
docker exec -it library-backend sh

# 进入MySQL容器
docker exec -it library-mysql bash

# 进入Redis容器
docker exec -it library-redis sh

# 进入Nginx容器
docker exec -it library-frontend sh
```

### 数据备份

```bash
# 备份MySQL数据库
docker exec library-mysql mysqldump -uroot -pyour_password library_management > backup_$(date +%Y%m%d_%H%M%S).sql

# 恢复MySQL数据库
docker exec -i library-mysql mysql -uroot -pyour_password library_management < backup.sql

# 备份Docker卷数据
docker run --rm -v library-management-system_mysql-data:/data -v $(pwd):/backup alpine tar czf /backup/mysql-data-backup.tar.gz /data
```

### 更新部署

```bash
# 拉取最新代码
git pull

# 重新构建并启动
docker-compose up -d --build

# 或只更新特定服务
docker-compose up -d --build backend
```

---

## 🐛 故障排查

### 问题1: 容器无法启动

```bash
# 查看容器详细状态
docker-compose ps -a

# 查看错误日志
docker-compose logs backend
docker-compose logs mysql
```

**常见原因：**
- 端口被占用：检查80/8080/3306/6379端口
- 磁盘空间不足：`df -h`
- 内存不足：`free -h`

### 问题2: 后端无法连接MySQL

```bash
# 检查MySQL容器状态
docker-compose logs mysql

# 进入后端容器测试连接
docker exec -it library-backend sh
ping mysql
nc -zv mysql 3306
```

**解决方案：**
- 确保MySQL容器健康：`docker-compose ps`
- 检查网络：`docker network ls`，确认在 `library-network` 中
- 等待MySQL完全启动（首次启动需要1-2分钟）

### 问题3: 前端无法访问后端API

```bash
# 检查Nginx配置
docker exec library-frontend cat /etc/nginx/conf.d/default.conf

# 测试API代理
curl http://localhost/api/health
```

**解决方案：**
- 检查nginx.conf中`proxy_pass`是否指向`backend:8080`
- 确认后端容器正常运行
- 检查浏览器控制台错误信息

### 问题4: 数据库初始化失败

```bash
# 查看MySQL初始化日志
docker-compose logs mysql | grep -i error

# 手动执行初始化脚本
docker exec -i library-mysql mysql -uroot -proot < backend/init_users.sql
```

**解决方案：**
- 确认SQL文件路径正确
- 检查SQL文件权限
- 手动执行SQL脚本

### 问题5: 内存不足

```bash
# 检查内存使用
free -h

# 检查Docker内存使用
docker stats --no-stream
```

**解决方案：**
- 增加服务器内存（建议至少2GB）
- 减少Maven构建内存：在Dockerfile中添加JVM参数
- 清理未使用的Docker镜像：`docker system prune -a`

---

## 🔐 安全建议

### 1. 修改默认密码

必须修改以下密码：
- MySQL root密码（docker-compose.yml中）
- JWT密钥（application.yml中）

### 2. 配置HTTPS

建议使用Let's Encrypt免费证书：
```bash
# 安装Certbot
sudo apt install certbot python3-certbot-nginx

# 获取证书
sudo certbot --nginx -d your-domain.com
```

修改nginx.conf配置HTTPS。

### 3. 限制端口访问

只开放必要端口：
```bash
# 云服务器安全组配置
- 80: HTTP（前端）
- 443: HTTPS（如配置）
- 22: SSH（建议改为其他端口）

# 内部服务端口不对外
- 3306: MySQL（仅容器内部访问）
- 6379: Redis（仅容器内部访问）
- 8080: 后端API（通过Nginx代理访问）
```

### 4. 定期备份

设置定时任务：
```bash
crontab -e

# 每天凌晨2点备份数据库
0 2 * * * cd /root/library-management-system && docker exec library-mysql mysqldump -uroot -pyour_password library_management > backup/db_$(date +\%Y\%m\%d).sql
```

---

## 📊 监控和日志

### 实时监控

```bash
# 监控所有容器
docker-compose logs -f

# 监控资源使用
docker stats

# 监控特定服务
docker-compose logs -f backend
```

### 日志文件位置

```bash
# Docker容器日志目录
/var/lib/docker/containers/

# 查看容器日志
ls -lh /var/lib/docker/containers/*/json.log
```

---

## 📞 获取帮助

如果遇到问题：

1. 查看日志：`docker-compose logs`
2. 检查容器状态：`docker-compose ps`
3. 查看官方文档：https://docs.docker.com/
4. 检查项目AGENTS.md文件

---

**部署完成后，记得：**
- ✅ 修改所有默认密码
- ✅ 配置HTTPS（生产环境）
- ✅ 设置定期备份
- ✅ 配置防火墙规则
- ✅ 监控服务器资源使用

祝部署顺利！🎉
