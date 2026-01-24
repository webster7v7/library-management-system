#!/bin/bash

# ============================================
# 图书馆管理系统 - 一键部署脚本
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
NC='\033[0m' # No Color

# 检查是否为root用户
if [ "$EUID" -ne 0 ]; then
    echo -e "${RED}错误: 请使用root用户运行此脚本${NC}"
    exit 1
fi

# 1. 检查Docker是否安装
echo -e "${YELLOW}[1/6] 检查Docker环境...${NC}"
if ! command -v docker &> /dev/null; then
    echo -e "${RED}Docker未安装${NC}"
    echo "请先安装Docker："
    echo "  Ubuntu/Debian: curl -fsSL https://get.docker.com | bash"
    echo "  CentOS/RHEL: yum install -y docker-ce"
    exit 1
fi
echo -e "${GREEN}✓ Docker已安装: $(docker --version)${NC}"

# 2. 检查Docker Compose
echo -e "${YELLOW}[2/6] 检查Docker Compose...${NC}"
if ! command -v docker-compose &> /dev/null; then
    echo -e "${YELLOW}Docker Compose未安装，正在安装...${NC}"
    sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    sudo chmod +x /usr/local/bin/docker-compose
    sudo ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose
fi
echo -e "${GREEN}✓ Docker Compose已安装: $(docker-compose --version)${NC}"

# 3. 检查端口占用
echo -e "${YELLOW}[3/6] 检查端口占用...${NC}"
PORTS=(80 3306 6379 8080)
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

# 4. 创建备份目录
echo -e "${YELLOW}[4/6] 创建备份目录...${NC}"
mkdir -p backup
echo -e "${GREEN}✓ 备份目录已创建${NC}"

# 5. 停止旧容器（如果存在）
echo -e "${YELLOW}[5/6] 停止旧容器...${NC}"
if docker-compose ps | grep -q "Up"; then
    echo "正在停止现有容器..."
    docker-compose down
    echo -e "${GREEN}✓ 旧容器已停止${NC}"
else
    echo -e "${GREEN}✓ 无运行中的容器${NC}"
fi

# 6. 构建并启动服务
echo -e "${YELLOW}[6/6] 构建并启动服务...${NC}"
echo "这可能需要几分钟，请耐心等待..."
echo ""

# 构建镜像并启动
docker-compose up -d --build

# 等待服务启动
echo ""
echo "等待服务启动..."
sleep 10

# 检查容器状态
echo ""
echo "检查容器状态..."
docker-compose ps

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
echo "⚠️  重要提示："
echo "  1. 请立即修改默认密码！"
echo "  2. 编辑 docker-compose.yml 修改 MYSQL_ROOT_PASSWORD"
echo "  3. 建议配置HTTPS（生产环境必须）"
echo ""
echo "常用命令："
echo "  查看日志: docker-compose logs -f"
echo "  停止服务: docker-compose down"
echo "  重启服务: docker-compose restart"
echo "  查看状态: docker-compose ps"
echo ""
echo "========================================"

# 提示是否查看日志
read -p "是否查看实时日志？(y/N) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    docker-compose logs -f
fi
