package com.good.physicalexercisesystem.vo;

import lombok.Data;

@Data
public class TeacherDashboardVO {
    /**
     * 学生统计
     */
    private StudentStats studentStats;
    
    /**
     * 课程统计
     */
    private CourseStats courseStats;
    
    /**
     * 本周统计
     */
    private WeekStats weekStats;
    
    /**
     * 待办统计
     */
    private TodoStats todoStats;
    
    @Data
    public static class StudentStats {
        private Integer total;      // 总学生数
        private Integer newCount;   // 新增学生数
        private Integer active;     // 活跃学生数
    }
    
    @Data
    public static class CourseStats {
        private Integer total;      // 总课程数
        private Integer active;     // 进行中课程数
        private Integer ended;      // 已结束课程数
    }
    
    @Data
    public static class WeekStats {
        private Integer hours;      // 总课时
        private Integer completed;  // 已完成课时
        private Integer upcoming;   // 待上课时
    }
    
    @Data
    public static class TodoStats {
        private Integer total;      // 总待办数
        private Integer attendance; // 考勤待办数
        private Integer evaluation; // 评价待办数
    }
} 