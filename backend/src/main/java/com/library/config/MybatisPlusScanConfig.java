package com.library.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis Plus 配置
 */
@Configuration
@MapperScan("com.library.mapper")
public class MybatisPlusScanConfig {
    // MyBatis Plus Mapper 扫描配置
}
