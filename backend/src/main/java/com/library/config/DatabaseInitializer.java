package com.library.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import java.util.stream.Collectors;

@Component
@Profile("!test")
public class DatabaseInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    private final JdbcTemplate jdbcTemplate;

    public DatabaseInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("========================================");
        logger.info("Starting database initialization...");
        logger.info("========================================");

        try {
            // 创建数据库（如果不存在）
            createDatabaseIfNotExists();

            // 执行 schema.sql
            executeSchemaSql();

            logger.info("========================================");
            logger.info("Database initialization completed successfully!");
            logger.info("========================================");
        } catch (Exception e) {
            logger.error("========================================");
            logger.error("Database initialization failed!", e);
            logger.error("========================================");
            // 不抛出异常，允许应用继续启动
        }
    }

    private void createDatabaseIfNotExists() {
        try {
            String dbName = extractDatabaseName();
            String urlWithoutDb = jdbcUrl.substring(0, jdbcUrl.lastIndexOf('/') + 1);

            logger.info("Connecting to MySQL server to create database '{}'...", dbName);

            // 使用不带数据库名的 URL 创建连接
            Connection conn = java.sql.DriverManager.getConnection(urlWithoutDb, username, password);
            Statement stmt = conn.createStatement();

            String createDbSql = String.format(
                "CREATE DATABASE IF NOT EXISTS `%s` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci",
                dbName
            );

            logger.info("Executing: {}", createDbSql);
            stmt.execute(createDbSql);

            stmt.close();
            conn.close();

            logger.info("Database '{}' created or already exists", dbName);
        } catch (Exception e) {
            logger.error("Failed to create database", e);
            throw new RuntimeException("Failed to create database: " + e.getMessage(), e);
        }
    }

    private void executeSchemaSql() {
        try {
            logger.info("Executing schema.sql...");

            ClassPathResource resource = new ClassPathResource("schema.sql");

            if (!resource.exists()) {
                logger.warn("schema.sql not found in classpath. Skipping schema execution.");
                return;
            }

            String sqlContent = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
            ).lines().collect(Collectors.joining("\n"));

            // 分割 SQL 语句
            String[] statements = sqlContent.split(";\\s*(?:(?i)DROP|(?i)CREATE|(?i)INSERT|(?i)ALTER|(?i)UPDATE|(?i)DELETE|(?i)USE)");

            for (String statement : statements) {
                String trimmed = statement.trim();

                // 跳过空语句和注释
                if (trimmed.isEmpty() || trimmed.startsWith("--") || trimmed.startsWith("/*")) {
                    continue;
                }

                try {
                    // 执行 SQL
                    if (!trimmed.endsWith(";")) {
                        trimmed += ";";
                    }

                    // 处理 USE 语句
                    if (trimmed.toUpperCase().startsWith("USE ")) {
                        continue; // USE 语句会由 Spring Boot JDBC 自动处理
                    }

                    logger.debug("Executing SQL: {}", trimmed.substring(0, Math.min(100, trimmed.length())) + "...");

                    try {
                        jdbcTemplate.execute(trimmed);
                    } catch (Exception e) {
                        // 记录错误但继续执行（允许部分失败，如表已存在）
                        logger.warn("SQL execution warning: {} - {}", e.getMessage(), trimmed.substring(0, Math.min(50, trimmed.length())));
                    }

                } catch (Exception e) {
                    logger.warn("Error processing statement: {}", trimmed.substring(0, Math.min(50, trimmed.length())));
                }
            }

            logger.info("schema.sql execution completed");

        } catch (Exception e) {
            logger.error("Failed to execute schema.sql", e);
            throw new RuntimeException("Failed to execute schema.sql: " + e.getMessage(), e);
        }
    }

    private String extractDatabaseName() {
        try {
            String url = jdbcUrl;

            // 移除查询参数
            int queryIndex = url.indexOf('?');
            if (queryIndex != -1) {
                url = url.substring(0, queryIndex);
            }

            // 提取最后一个 / 后的数据库名
            int lastSlash = url.lastIndexOf('/');
            if (lastSlash != -1 && lastSlash < url.length() - 1) {
                return url.substring(lastSlash + 1);
            }

            throw new IllegalArgumentException("Could not extract database name from URL: " + jdbcUrl);
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract database name", e);
        }
    }
}
