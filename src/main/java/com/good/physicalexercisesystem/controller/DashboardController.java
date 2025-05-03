package com.good.physicalexercisesystem.controller;

import com.good.physicalexercisesystem.common.CommonResult;
import com.good.physicalexercisesystem.dto.DashboardStatisticsDTO;
import com.good.physicalexercisesystem.entity.Notice;
import com.good.physicalexercisesystem.entity.SysLog;
import com.good.physicalexercisesystem.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/statistics")
    public CommonResult<DashboardStatisticsDTO> getStatistics() {
        return CommonResult.success(dashboardService.getStatistics());
    }

    @GetMapping("/notices")
    public CommonResult<List<Notice>> getLatestNotices() {
        return CommonResult.success(dashboardService.getLatestNotices());
    }

    @GetMapping("/logs")
    public CommonResult<List<SysLog>> getLatestLogs() {
        return CommonResult.success(dashboardService.getLatestLogs());
    }

    @GetMapping("/all")
    public CommonResult<Map<String, Object>> getAllDashboardData() {
        Map<String, Object> data = new HashMap<>();
        data.put("statistics", dashboardService.getStatistics());
        data.put("latestNotices", dashboardService.getLatestNotices());
        data.put("latestLogs", dashboardService.getLatestLogs());
        return CommonResult.success(data);
    }
}
