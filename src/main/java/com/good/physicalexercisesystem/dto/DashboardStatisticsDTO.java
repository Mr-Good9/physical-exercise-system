package com.good.physicalexercisesystem.dto;

import lombok.Data;

@Data
public class DashboardStatisticsDTO {
    private Integer totalUsers;
    private Integer teacherCount;
    private Integer studentCount;
    private Integer activeToday;
    private Double activeGrowth;
    private Integer noticeCount;
    private Integer unreadNotice;
    private String uptime;
}
