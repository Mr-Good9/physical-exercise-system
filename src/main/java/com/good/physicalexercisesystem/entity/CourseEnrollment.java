package com.good.physicalexercisesystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("pe_course_enrollment")
public class CourseEnrollment {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 学生ID
     */
    private Long studentId;
    /**
     * 课程ID
     */
    private Long courseId;
    /**
     * 状态:enrolled-已选,dropped-已退
     */
    private String status; // enrolled, dropped

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
