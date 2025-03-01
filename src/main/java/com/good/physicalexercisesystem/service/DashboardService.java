package com.good.physicalexercisesystem.service;

import com.good.physicalexercisesystem.vo.DashboardVO;

public interface DashboardService {
    /**
     * 获取学生仪表盘统计数据
     * @return 统计数据
     */
    DashboardVO getStudentDashboardStatistics();
} 