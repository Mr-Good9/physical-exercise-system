package com.good.physicalexercisesystem.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TeacherCourseVO {
    private Long id;
    private String name;
    private String description;
    private Long teacherId;
    private String teacherName;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private Integer attendanceCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean deleted;
} 