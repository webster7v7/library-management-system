# PowerShell 后端重启脚本
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  图书馆管理系统 - 后端重启脚本" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 1. 查找监听 8080 端口的进程
Write-Host "[1/4] 查找后端进程..." -ForegroundColor Yellow
$connection = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue | Select-Object -First 1

if (-not $connection) {
    Write-Host "    错误：未找到监听 8080 端口的进程" -ForegroundColor Red
    Write-Host "    后端可能未运行" -ForegroundColor Yellow
    Read-Host "按回车键退出"
    exit 1
}

$pid = $connection.OwningProcess
$process = Get-Process -Id $pid -ErrorAction SilentlyContinue

if ($process) {
    Write-Host "    找到进程：" -ForegroundColor Green -NoNewline
    Write-Host " $pid ($($process.ProcessName))" -ForegroundColor White
} else {
    Write-Host "    警告：进程 ID $pid 存在但无法获取详细信息" -ForegroundColor Yellow
}

Write-Host ""

# 2. 停止进程
Write-Host "[2/4] 停止后端进程..." -ForegroundColor Yellow
try {
    Stop-Process -Id $pid -Force -ErrorAction Stop
    Write-Host "    进程已停止" -ForegroundColor Green
} catch {
    Write-Host "    错误：无法停止进程 - $_" -ForegroundColor Red
    Read-Host "按回车键退出"
    exit 1
}

Write-Host ""

# 3. 等待端口释放
Write-Host "[3/4] 等待端口释放..." -ForegroundColor Yellow
for ($i = 1; $i -le 5; $i++) {
    $stillListening = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue
    if (-not $stillListening) {
        Write-Host "    端口已释放" -ForegroundColor Green
        break
    }
    Start-Sleep -Seconds 1
    Write-Host "    等待... ($i/5)" -ForegroundColor Gray
}

Write-Host ""

# 4. 提示重启
Write-Host "[4/4] 后端已停止" -ForegroundColor Green
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  现在请在 IDE 中重新启动后端" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "或使用命令行：" -ForegroundColor Yellow
Write-Host ""
Write-Host "  cd backend" -ForegroundColor White
Write-Host "  mvn spring-boot:run" -ForegroundColor White
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 等待用户确认启动
Write-Host "后端重新启动后，按回车键进行验证..." -ForegroundColor Yellow
Read-Host

# 验证
Write-Host ""
Write-Host "验证中..." -ForegroundColor Yellow
Start-Sleep -Seconds 2

$connection = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue | Select-Object -First 1
if ($connection) {
    Write-Host ""
    Write-Host "    [SUCCESS] 后端已重新启动" -ForegroundColor Green
    Write-Host "    端口 8080 正在监听" -ForegroundColor Green
    Write-Host ""

    # 测试调试接口
    Write-Host "    测试调试接口..." -ForegroundColor Yellow
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8080/api/debug/users" -UseBasicParsing -TimeoutSec 5
        if ($response.StatusCode -eq 200) {
            Write-Host "    [SUCCESS] 调试接口响应正常" -ForegroundColor Green
            Write-Host ""
            Write-Host "    现在可以测试登录功能了！" -ForegroundColor Cyan
        }
    } catch {
        Write-Host "    [WARNING] 调试接口尚未就绪，请稍等" -ForegroundColor Yellow
    }
} else {
    Write-Host ""
    Write-Host "    [INFO] 端口 8080 尚未监听" -ForegroundColor Yellow
    Write-Host "    请等待后端启动完成" -ForegroundColor Yellow
}

Write-Host ""
Read-Host "按回车键退出"
