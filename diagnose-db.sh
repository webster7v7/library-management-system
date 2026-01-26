#!/bin/bash

# Ubuntu 云服务器数据库连接诊断脚本
# 用于诊断 Docker 容器无法连接宿主机 MySQL 的问题

echo "========================================"
echo "数据库连接诊断工具"
echo "========================================"
echo ""

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 检查函数
check_pass() {
    echo -e "${GREEN}✓ $1${NC}"
}

check_fail() {
    echo -e "${RED}✗ $1${NC}"
}

check_warn() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

# 1. 检查 Docker 版本
echo "[1/7] 检查 Docker 版本..."
DOCKER_VERSION=$(docker --version 2>/dev/null)
if [ $? -eq 0 ]; then
    check_pass "Docker 已安装: $DOCKER_VERSION"
else
    check_fail "Docker 未安装"
    exit 1
fi
echo ""

# 2. 检查 Docker 网络配置
echo "[2/7] 检查 Docker 网络配置..."
GATEWAY_IP=$(ip route show | grep default | awk '{print $3}')
DOCKER_GATEWAY=$(docker network inspect bridge 2>/dev/null | grep -oP '"Gateway": "\K[^"]*' | head -1)
if [ -n "$DOCKER_GATEWAY" ]; then
    check_pass "Docker 桥接网关 IP: $DOCKER_GATEWAY"
else
    check_fail "无法获取 Docker 网关 IP"
fi
echo ""

# 3. 检查 MySQL 监听状态
echo "[3/7] 检查 MySQL 监听状态..."
MYSQL_LISTEN=$(sudo netstat -tlnp 2>/dev/null | grep :3306 || ss -tlnp 2>/dev/null | grep :3306)
if [ -z "$MYSQL_LISTEN" ]; then
    check_fail "MySQL 未在 3306 端口监听"
    echo "   请确保 MySQL 已启动: sudo systemctl status mysql"
else
    BIND_ADDRESS=$(echo "$MYSQL_LISTEN" | awk '{print $4}' | cut -d: -f1)
    if [ "$BIND_ADDRESS" = "0.0.0.0" ]; then
        check_pass "MySQL 监听在所有接口 (0.0.0.0:3306)"
    elif [ "$BIND_ADDRESS" = "127.0.0.1" ]; then
        check_fail "MySQL 仅监听本地 (127.0.0.1:3306)"
        echo "   ❌ 容器无法连接！需要修改为 0.0.0.0"
    else
        check_warn "MySQL 监听在 $BIND_ADDRESS:3306"
    fi
fi
echo ""

# 4. 检查 MySQL 服务状态
echo "[4/7] 检查 MySQL 服务状态..."
if systemctl is-active --quiet mysql; then
    check_pass "MySQL 服务运行中"
else
    check_fail "MySQL 服务未运行"
    echo "   启动命令: sudo systemctl start mysql"
fi
echo ""

# 5. 检查防火墙规则
echo "[5/7] 检查防火墙规则..."
if command -v ufw &> /dev/null; then
    UFW_STATUS=$(sudo ufw status | head -1)
    echo "   UFW 状态: $UFW_STATUS"

    # 检查是否允许 3306 端口
    if sudo ufw status | grep -q "3306"; then
        check_pass "防火墙允许 3306 端口"
    else
        check_warn "防火墙可能阻止 3306 端口"
    fi

    # 检查是否允许 Docker 网络
    if sudo ufw status | grep -q "172.17.0.0/16"; then
        check_pass "防火墙允许 Docker 网络 (172.17.0.0/16)"
    else
        check_warn "防火墙未显式允许 Docker 网络"
        echo "   建议: sudo ufw allow from 172.17.0.0/16"
    fi
elif command -v iptables &> /dev/null; then
    echo "   使用 iptables"
    if sudo iptables -L -n | grep -q "ACCEPT.*dpt:3306"; then
        check_pass "防火墙允许 3306 端口"
    else
        check_warn "iptables 可能阻止 3306 端口"
    fi
else
    echo "   未检测到防火墙管理工具 (ufw/iptables)"
fi
echo ""

# 6. 测试从宿主机连接 MySQL
echo "[6/7] 测试从宿主机连接 MySQL..."
read -p "MySQL 密码: " -s MYSQL_PASSWORD
echo ""
MYSQL_TEST=$(mysql -h 127.0.0.1 -P 3306 -u root -p"$MYSQL_PASSWORD" -e "SELECT 1;" 2>&1)
if [ $? -eq 0 ]; then
    check_pass "宿主机可以连接 MySQL"
else
    check_fail "宿主机无法连接 MySQL"
    echo "   错误: $MYSQL_TEST"
fi
echo ""

# 7. 测试从 Docker 容器内部连接
echo "[7/7] 测试从 Docker 容器内部连接 MySQL..."
DOCKER_TEST=$(docker run --rm --network bridge alpine sh -c "apk add --no-cache mysql-client >/dev/null 2>&1 && mysql -h host.docker.internal -P 3306 -u root -p'$MYSQL_PASSWORD' -e 'SELECT 1;' 2>&1")
if [ $? -eq 0 ]; then
    check_pass "Docker 容器可以连接 MySQL"
else
    check_fail "Docker 容器无法连接 MySQL"
    echo "   错误: $DOCKER_TEST"
    echo ""
    echo "   尝试直接连接网关 IP..."
    DOCKER_TEST2=$(docker run --rm --network bridge alpine sh -c "apk add --no-cache mysql-client >/dev/null 2>&1 && mysql -h 172.17.0.1 -P 3306 -u root -p'$MYSQL_PASSWORD' -e 'SELECT 1;' 2>&1)
    if [ $? -eq 0 ]; then
        check_pass "通过网关 IP (172.17.0.1) 可以连接"
        echo "   说明 DNS 解析有问题"
    else
        check_fail "通过网关 IP 也无法连接"
        echo "   错误: $DOCKER_TEST2"
    fi
fi
echo ""

# 诊断总结
echo "========================================"
echo "诊断总结"
echo "========================================"
echo ""
echo "如果第 [3/7] 步失败（MySQL 仅监听 127.0.0.1），请执行："
echo "  1. 编辑配置: sudo vim /etc/mysql/mysql.conf.d/mysqld.cnf"
echo "  2. 修改 bind-address = 0.0.0.0"
echo "  3. 重启服务: sudo systemctl restart mysql"
echo ""
echo "如果第 [5/7] 步警告（防火墙），请执行："
echo "  sudo ufw allow from 172.17.0.0/16"
echo ""
echo "如果第 [7/7] 步失败（容器无法连接），请检查："
echo "  1. MySQL 用户是否允许来自 172.17.0.1 的连接"
echo "  2. 防火墙是否阻止了 Docker 网络"
echo ""
