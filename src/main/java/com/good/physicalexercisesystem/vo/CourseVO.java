package com.good.physicalexercisesystem.vo;

import lombok.Data;

@Data
public class CourseVO {
    private Long id;
    private String name;
    private Long teacherId;
    private String teacherName;
    private String type;
    private String time;
    private String location;
    private Integer capacity;
    private Integer enrolled;
    private String description;
    private Boolean isEnrolled;  // 当前学生是否已选
} 