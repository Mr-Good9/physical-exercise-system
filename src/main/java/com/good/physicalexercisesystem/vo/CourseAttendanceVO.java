package com.good.physicalexercisesystem.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CourseAttendanceVO {
    /**
     * 课程ID
     */
    private Long id;
    /**
     * 课程名称
     */
    private String studentName;
    /**
     * 学生ID
     */
    private String studentId;
    /**
     * 课程日期
     */
    private LocalDate attendanceDate;
    /**
     * 出勤状态
     */
    private String status;
    /**
     * 备注
     */
    private String remark;
}
