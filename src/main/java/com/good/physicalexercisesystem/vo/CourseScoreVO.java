package com.good.physicalexercisesystem.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CourseScoreVO {
    /**
     * 课程id
     */
    private Long id;
    /**
     * 学生姓名
     */
    private String studentName;
    /**
     * 学生Id
     */
    private String studentId;
    /**
     * 课程分数
     */
    private BigDecimal score;
    /**
     * 评价等级
     */
    private String evaluation;
    /**
     * 教师评语
     */
    private String teacherComment;
    /**
     * 成绩类型:regular-平时成绩,midterm-期中成绩,final-期末成绩
     */
    private String scoreType;
}
