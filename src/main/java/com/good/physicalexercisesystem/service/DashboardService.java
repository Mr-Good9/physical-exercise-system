package com.good.physicalexercisesystem.service;

import com.good.physicalexercisesystem.dto.DashboardStatisticsDTO;
import com.good.physicalexercisesystem.entity.Notice;
import com.good.physicalexercisesystem.entity.SysLog;

import java.util.List;

public interface DashboardService {

    DashboardStatisticsDTO getStatistics();

    List<Notice> getLatestNotices();

    List<SysLog> getLatestLogs();
}
