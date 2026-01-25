#!/bin/bash

# ============================================
# 图书馆管理系统 - 云服务器一键部署脚本
# ============================================

set -e  # 遇到错误立即退出

echo "========================================"
echo "  图书馆管理系统 - 云服务器部署脚本"
echo "========================================"
echo ""

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 获取脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

echo -e "${BLUE}项目目录: $PROJECT_DIR${NC}"
echo ""

# 检查是否为root用户
if [ "$EUID" -ne 0 ]; then
    echo -e "${RED}错误: 请使用root用户运行此脚本${NC}"
    exit 1
fi

# 1. 检查Docker是否安装
echo -e "${YELLOW}[1/7] 检查Docker环境...${NC}"
if ! command -v docker &> /dev/null; then
    echo -e "${RED}Docker未安装${NC}"
    echo "请先安装Docker："
    echo "  Ubuntu/Debian: curl -fsSL https://get.docker.com | bash"
    echo "  CentOS/RHEL: yum install -y docker-ce"
    exit 1
fi
echo -e "${GREEN}✓ Docker已安装: $(docker --version)${NC}"

# 2. 检查Docker Compose
echo -e "${YELLOW}[2/7] 检查Docker Compose...${NC}"
if ! docker compose version &> /dev/null; then
    if command -v docker-compose &> /dev/null; then
        echo -e "${GREEN}✓ Docker Compose已安装: $(docker-compose --version)${NC}"
        COMPOSE_CMD="docker-compose"
    else
        echo -e "${RED}Docker Compose未安装${NC}"
        echo "请先安装Docker Compose："
        echo "  Ubuntu/Debian: curl -L 'https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)' -o /usr/local/bin/docker-compose"
        echo "  chmod +x /usr/local/bin/docker-compose"
        exit 1
    fi
else
    echo -e "${GREEN}✓ Docker Compose已安装: $(docker compose version)${NC}"
    COMPOSE_CMD="docker compose"
fi

# 3. 检查端口占用
echo -e "${YELLOW}[3/7] 检查端口占用...${NC}"
PORTS=(80 8080)
OCCUPIED_PORTS=()

for port in "${PORTS[@]}"; do
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
        OCCUPIED_PORTS+=($port)
        echo -e "${RED}✗ 端口 $port 已被占用${NC}"
    fi
done

if [ ${#OCCUPIED_PORTS[@]} -gt 0 ]; then
    echo -e "${RED}以下端口被占用，请先停止相关服务:${NC}"
    printf '%s\n' "${OCCUPIED_PORTS[@]}"
    read -p "是否继续？(y/N) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
else
    echo -e "${GREEN}✓ 所有端口可用${NC}"
fi

# 4. 检查 MySQL 连接
echo -e "${YELLOW}[4/7] 检查 MySQL 连接...${NC}"
if command -v mysql &> /dev/null; then
    echo -e "${GREEN}✓ MySQL 客户端已安装${NC}"
    read -p "是否测试 MySQL 连接？(Y/n) " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]] || [ -z "$REPLY" ]; then
        read -p "MySQL 密码: " -s MYSQL_PASSWORD_TEST
        echo
        if mysql -u root -p"$MYSQL_PASSWORD_TEST" -h 127.0.0.1 -e "SELECT 1;" &> /dev/null; then
            echo -e "${GREEN}✓ MySQL 连接成功${NC}"
        else
            echo -e "${RED}✗ MySQL 连接失败，请检查密码${NC}"
            read -p "是否继续？(y/N) " -n 1 -r
            echo
            if [[ ! $REPLY =~ ^[Yy]$ ]]; then
                exit 1
            fi
        fi
    fi
else
    echo -e "${YELLOW}MySQL 客户端未安装，跳过连接测试${NC}"
fi

# 5. 检查 Redis 连接
echo -e "${YELLOW}[5/7] 检查 Redis 连接...${NC}"
if command -v redis-cli &> /dev/null; then
    echo -e "${GREEN}✓ Redis 客户端已安装${NC}"
    if redis-cli -h 127.0.0.1 -p 6379 -a "redis_AG2sFZ" ping &> /dev/null; then
        echo -e "${GREEN}✓ Redis 连接成功${NC}"
    else
        echo -e "${YELLOW}⚠ Redis 连接测试失败，但容器可能仍能连接${NC}"
    fi
else
    echo -e "${YELLOW}Redis 客户端未安装，跳过连接测试${NC}"
fi

# 6. 停止旧容器（如果存在）
echo -e "${YELLOW}[6/7] 停止旧容器...${NC}"
cd "$PROJECT_DIR/docker"

if [ "$COMPOSE_CMD" = "docker compose" ]; then
    COMPOSE_FILE="docker-compose.prod.yml"
else
    COMPOSE_FILE="docker-compose.prod.yml"
fi

if $COMPOSE_CMD -f "$COMPOSE_FILE" ps | grep -q "Up"; then
    echo "正在停止现有容器..."
    $COMPOSE_CMD -f "$COMPOSE_FILE" down
    echo -e "${GREEN}✓ 旧容器已停止${NC}"
else
    echo -e "${GREEN}✓ 无运行中的容器${NC}"
fi

# 7. 构建并启动服务
echo -e "${YELLOW}[7/7] 构建并启动服务...${NC}"
echo "这可能需要几分钟，请耐心等待..."
echo ""

# 构建镜像并启动
$COMPOSE_CMD -f "$COMPOSE_FILE" up -d --build

# 等待服务启动
echo ""
echo "等待服务启动..."
sleep 15

# 检查容器状态
echo ""
echo "检查容器状态..."
$COMPOSE_CMD -f "$COMPOSE_FILE" ps

# 显示部署结果
echo ""
echo "========================================"
echo -e "${GREEN}  部署完成！${NC}"
echo "========================================"
echo ""
echo "访问地址："
echo "  前端: http://$(hostname -I | awk '{print $1}')"
echo "  API文档: http://$(hostname -I | awk '{print $1}'):8080/swagger-ui.html"
echo ""
echo "默认账号："
echo "  管理员: admin / admin"
echo "  普通用户: user / user"
echo ""
echo -e "${BLUE}⚠️  重要提示：${NC}"
echo "  1. .env 文件已包含所有配置（已自动设置）"
echo "  2. 建议立即修改默认密码"
echo "  3. 建议配置HTTPS（生产环境必须）"
echo "  4. 确保防火墙开放端口 80 和 8080"
echo ""
echo "常用命令："
echo "  查看日志: $COMPOSE_CMD -f $COMPOSE_FILE logs -f"
echo "  查看后端日志: docker logs library-backend"
echo "  查看前端日志: docker logs library-frontend"
echo "  停止服务: $COMPOSE_CMD -f $COMPOSE_FILE down"
echo "  重启服务: $COMPOSE_CMD -f $COMPOSE_FILE restart"
echo "  查看状态: $COMPOSE_CMD -f $COMPOSE_FILE ps"
echo ""

# 提示是否查看日志
read -p "是否查看实时日志？(y/N) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    $COMPOSE_CMD -f "$COMPOSE_FILE" logs -f
fi
