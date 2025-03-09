package com.good.physicalexercisesystem.dto;

import lombok.Data;

@Data
public class ClassDTO {
    private Long id;
    private String className;
    private String grade;
    private String semester;
    private Integer studentCount;
    private Long teacherId;
    private String teacherName;
} 