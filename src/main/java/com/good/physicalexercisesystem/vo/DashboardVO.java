package com.good.physicalexercisesystem.vo;

import lombok.Data;

@Data
public class DashboardVO {
    // 课程统计
    private Integer totalCourses;      // 总课程数
    private Integer requiredCourses;   // 必修课数
    private Integer optionalCourses;   // 选修课数
    
    // 体测统计
    private Double latestScore;        // 最近一次体测成绩
    private String latestTestType;     // 最近一次测试项目
    private Double averageScore;       // 平均成绩
    private Integer totalTests;        // 总测试次数
    private Integer passedTests;       // 及格次数
    private Double passRate;           // 及格率
    
    // 趋势数据
    private Double monthOverMonth;     // 环比变化
    private Double yearOverYear;       // 同比变化
} 