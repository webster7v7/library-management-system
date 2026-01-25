package com.library.controller;

import com.library.common.Result;
import com.library.dto.DashboardResponse;
import com.library.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "仪表盘", description = "仪表盘统计相关接口")
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    private DashboardService dashboardService;

    @GetMapping
    @Operation(summary = "获取仪表盘数据")
    public Result<DashboardResponse> getDashboardData() {
        try {
            logger.info("Fetching dashboard data");
            DashboardResponse data = dashboardService.getDashboardData();
            return Result.success(data);
        } catch (Exception e) {
            logger.error("Failed to fetch dashboard data", e);
            return Result.error(500, "获取仪表盘数据失败: " + e.getMessage());
        }
    }
}
