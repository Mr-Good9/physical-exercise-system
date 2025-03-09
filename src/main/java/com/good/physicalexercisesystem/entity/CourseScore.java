package com.good.physicalexercisesystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("pe_course_score")
public class CourseScore {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 课程id
     */
    private Long courseId;
    /**
     * 学生id
     */
    private Long studentId;
    /**
     * 分数
     */
    private BigDecimal score;
    /**
     * 评价
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
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean deleted;
}
