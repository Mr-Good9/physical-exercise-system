package com.good.physicalexercisesystem.vo;

import lombok.Data;

@Data
public class CourseQuery {
    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 课程类型
     */
    private String courseType;

    /**
     * 课程状态
     */
    private String status;
}
