package com.good.physicalexercisesystem.vo;

import lombok.Data;

@Data
public class ClassVO {
    /**
     * 班级ID
     */
    private Long id;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 年级
     */
    private String grade;

    /**
     * 班主任ID
     */
    private Long teacherId;

    /**
     * 班主任姓名
     */
    private String teacherName;

    /**
     * 学期
     */
    private String semester;
} 