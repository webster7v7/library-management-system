# 图书馆管理系统 - Docker 部署快速指南

## 🚀 快速部署（三步搞定）

### 第一步：上传项目到云服务器

```bash
# 方法1：使用SCP上传（在本地执行）
scp -r "D:\programming\project\library management system" root@your-server-ip:/root/library-management-system

# 方法2：使用Git克隆（在云服务器上执行）
git clone <your-git-repository-url> /root/library-management-system
cd /root/library-management-system
```

### 第二步：安装Docker（如果未安装）

```bash
# Ubuntu/Debian 一键安装
curl -fsSL https://get.docker.com | bash

# 添加Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
sudo ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose

# 验证安装
docker --version
docker-compose --version
```

### 第三步：一键部署

```bash
# 进入项目目录
cd /root/library-management-system

# 运行一键部署脚本
./deploy.sh

# 或者手动启动
docker-compose up -d --build
```

就这么简单！🎉

---

## 📱 访问系统

部署成功后：

- **前端地址**: `http://your-server-ip`
- **API文档**: `http://your-server-ip:8080/swagger-ui.html`

**默认账号：**
- 管理员: `admin` / `admin`
- 普通用户: `user` / `user`

---

## ⚠️ 安全警告

**立即修改默认密码！**

编辑 `docker-compose.yml`：
```bash
vim docker-compose.yml
```

找到并修改：
```yaml
environment:
  MYSQL_ROOT_PASSWORD: your_secure_password_here  # 改为强密码！
```

---

## 🔧 常用命令

```bash
# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f

# 重启服务
docker-compose restart

# 停止服务
docker-compose down

# 查看后端日志
docker-compose logs -f backend

# 查看MySQL日志
docker-compose logs mysql
```

---

## ❓ 遇到问题？

1. **查看日志**: `docker-compose logs -f`
2. **检查端口**: `netstat -tlnp`
3. **查看状态**: `docker-compose ps`
4. **重启服务**: `docker-compose restart`

详细文档请查看 [DEPLOYMENT.md](./DEPLOYMENT.md)

---

## 📞 需要帮助？

- 查看完整部署文档: `DEPLOYMENT.md`
- 查看开发指南: `AGENTS.md`
- 项目README: `README.md`
