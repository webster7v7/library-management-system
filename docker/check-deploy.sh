#!/bin/bash

# ============================================
# 图书馆管理系统 - 部署前检查脚本
# ============================================

set -e

echo "========================================"
echo "  部署前环境检查"
echo "========================================"
echo ""

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# 获取脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

echo -e "${BLUE}项目目录: $PROJECT_DIR${NC}"
echo ""

# 1. 检查 Docker 版本
echo -e "${YELLOW}[1/4] 检查 Docker 版本...${NC}"
DOCKER_VERSION=$(docker --version | awk '{print $3}' | sed 's/,//')
DOCKER_MAJOR=$(echo $DOCKER_VERSION | cut -d. -f1)
DOCKER_MINOR=$(echo $DOCKER_VERSION | cut -d. -f2)

echo -e "${GREEN}✓ Docker 版本: $DOCKER_VERSION${NC}"

if [ "$DOCKER_MAJOR" -lt 20 ] || ([ "$DOCKER_MAJOR" -eq 20 ] && [ "$DOCKER_MINOR" -lt 10 ]); then
    echo -e "${RED}✗ Docker 版本过低，需要 20.10 或更高版本${NC}"
    echo "当前版本不支持 host-gateway 功能"
    echo "请升级 Docker 或使用其他连接方式"
    exit 1
else
    echo -e "${GREEN}✓ Docker 版本支持 host-gateway${NC}"
fi

# 2. 检查 Docker Compose
echo -e "${YELLOW}[2/4] 检查 Docker Compose...${NC}"
if docker compose version &> /dev/null; then
    echo -e "${GREEN}✓ Docker Compose 已安装: $(docker compose version)${NC}"
    COMPOSE_CMD="docker compose"
elif command -v docker-compose &> /dev/null; then
    echo -e "${GREEN}✓ Docker Compose 已安装: $(docker-compose --version)${NC}"
    COMPOSE_CMD="docker-compose"
else
    echo -e "${RED}✗ Docker Compose 未安装${NC}"
    exit 1
fi

# 3. 检查 MySQL 连接
echo -e "${YELLOW}[3/4] 检查 MySQL 连接...${NC}"
if command -v mysql &> /dev/null; then
    if mysql -u root -p"mysql_ZAxX6G" -h 127.0.0.1 -e "SELECT 1;" &> /dev/null; then
        echo -e "${GREEN}✓ MySQL 连接成功${NC}"
    else
        echo -e "${RED}✗ MySQL 连接失败${NC}"
        echo "请检查 MySQL 密码和配置"
        exit 1
    fi
else
    echo -e "${YELLOW}⚠ MySQL 客户端未安装，跳过连接测试${NC}"
fi

# 4. 检查 Redis 连接
echo -e "${YELLOW}[4/4] 检查 Redis 连接...${NC}"
if command -v redis-cli &> /dev/null; then
    if redis-cli -h 127.0.0.1 -p 6379 -a "redis_AG2sFZ" ping &> /dev/null; then
        echo -e "${GREEN}✓ Redis 连接成功${NC}"
    else
        echo -e "${RED}✗ Redis 连接失败${NC}"
        echo "请检查 Redis 密码和配置"
        exit 1
    fi
else
    echo -e "${YELLOW}⚠ Redis 客户端未安装，跳过连接测试${NC}"
fi

echo ""
echo "========================================"
echo -e "${GREEN}  所有检查通过！可以开始部署${NC}"
echo "========================================"
echo ""
echo "部署命令："
echo "  cd $SCRIPT_DIR"
echo "  $COMPOSE_CMD -f docker-compose.prod.yml up -d --build"
echo ""