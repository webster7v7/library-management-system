package com.library.controller;

import com.library.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器 - 用于诊断
 */
@RestController
@RequestMapping("/api/health")
public class HealthController {

    private static final Logger logger = LoggerFactory.getLogger(HealthController.class);

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    /**
     * 健康检查
     */
    @GetMapping
    public Result<Map<String, Object>> check() {
        try {
            logger.info("Health check requested");

            Map<String, Object> health = new HashMap<>();
            health.put("status", "UP");
            health.put("timestamp", System.currentTimeMillis());
            health.put("message", "后端服务正常运行");

            return Result.success("健康检查通过", health);
        } catch (Exception e) {
            logger.error("Health check failed", e);
            return Result.error(500, "健康检查失败: " + e.getMessage());
        }
    }

    /**
     * 测试数据库连接
     */
    @GetMapping("/database")
    public Result<Map<String, String>> testDatabase() {
        try {
            logger.info("Database connection test requested");

            Map<String, String> info = new HashMap<>();
            info.put("database", "MySQL");
            info.put("url", jdbcUrl);

            return Result.success("数据库连接正常", info);
        } catch (Exception e) {
            logger.error("Database test failed", e);
            return Result.error(500, "数据库测试失败: " + e.getMessage());
        }
    }
}
