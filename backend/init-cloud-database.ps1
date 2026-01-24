# 图书馆管理系统 - 云服务器数据库初始化脚本

# 数据库配置
$MYSQL_HOST = "103.43.8.83"
$MYSQL_PORT = "3306"
$MYSQL_USER = "root"
$MYSQL_PASSWORD = "mysql_ZAxX6G"
$MYSQL_DATABASE = "library_management"
$SCHEMA_FILE = "d:\programming\实战\图书馆管理系统 trae\backend\src\main\resources\schema.sql"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  图书馆管理系统 - 云服务器数据库初始化" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "数据库配置:" -ForegroundColor Yellow
Write-Host "  主机: $MYSQL_HOST"
Write-Host "  端口: $MYSQL_PORT"
Write-Host "  用户: $MYSQL_USER"
Write-Host "  数据库: $MYSQL_DATABASE"
Write-Host ""

# 检查MySQL客户端
Write-Host "[1/3] 检查MySQL客户端..." -ForegroundColor Green
$mysqlPath = Get-Command mysql -ErrorAction SilentlyContinue
if (-not $mysqlPath) {
    Write-Host "错误: 未找到MySQL客户端" -ForegroundColor Red
    Write-Host ""
    Write-Host "请安装MySQL客户端或使用以下方法之一:" -ForegroundColor Yellow
    Write-Host "  1. 下载MySQL: https://dev.mysql.com/downloads/mysql/"
    Write-Host "  2. 使用IntelliJ IDEA的数据库工具连接并执行schema.sql"
    Write-Host "  3. 使用云服务器上的MySQL客户端直接执行"
    Write-Host ""
    Read-Host "按回车键退出"
    exit 1
}
Write-Host "MySQL客户端检查通过" -ForegroundColor Green

# 检查MySQL连接
Write-Host ""
Write-Host "[2/3] 检查MySQL连接..." -ForegroundColor Green
try {
    $result = & mysql -h $MYSQL_HOST -P $MYSQL_PORT -u $MYSQL_USER -p$MYSQL_PASSWORD -e "SELECT 1;" 2>&1
    if ($LASTEXITCODE -ne 0) {
        throw "连接失败"
    }
} catch {
    Write-Host "错误: 无法连接到MySQL数据库" -ForegroundColor Red
    Write-Host ""
    Write-Host "请检查:" -ForegroundColor Yellow
    Write-Host "  1. 云服务器MySQL服务是否启动"
    Write-Host "  2. 网络连接是否正常"
    Write-Host "  3. 用户名密码是否正确"
    Write-Host "  4. 防火墙是否开放3306端口"
    Write-Host ""
    Write-Host "错误详情: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host ""
    Read-Host "按回车键退出"
    exit 1
}
Write-Host "MySQL连接成功" -ForegroundColor Green

# 执行数据库初始化
Write-Host ""
Write-Host "[3/3] 初始化数据库..." -ForegroundColor Green
try {
    $sqlContent = Get-Content $SCHEMA_FILE -Raw -Encoding UTF8
    $result = & mysql -h $MYSQL_HOST -P $MYSQL_PORT -u $MYSQL_USER -p$MYSQL_PASSWORD -e $sqlContent 2>&1
    if ($LASTEXITCODE -ne 0) {
        throw "初始化失败"
    }
} catch {
    Write-Host "错误: 数据库初始化失败" -ForegroundColor Red
    Write-Host ""
    Write-Host "错误详情: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host ""
    Read-Host "按回车键退出"
    exit 1
}
Write-Host "数据库初始化成功" -ForegroundColor Green

# 验证数据
Write-Host ""
Write-Host "验证初始化数据..." -ForegroundColor Green
try {
    $result = & mysql -h $MYSQL_HOST -P $MYSQL_PORT -u $MYSQL_USER -p$MYSQL_PASSWORD -e "USE $MYSQL_DATABASE; SELECT COUNT(*) as '用户数量' FROM sys_user; SELECT COUNT(*) as '分类数量' FROM book_category;" 2>&1
    Write-Host $result
} catch {
    Write-Host "警告: 数据验证失败，但初始化可能已成功" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  数据库初始化完成！" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "默认账号：" -ForegroundColor Green
Write-Host "  - 管理员: admin / admin"
Write-Host "  - 普通用户: user / user"
Write-Host ""
Write-Host "数据库信息：" -ForegroundColor Green
Write-Host "  - 数据库名: $MYSQL_DATABASE"
Write-Host "  - 用户表: sys_user"
Write-Host "  - 图书表: book"
Write-Host "  - 分类表: book_category"
Write-Host ""
Read-Host "按回车键退出"
