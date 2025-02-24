package com.good.physicalexercisesystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("pe_course_enrollment")
public class CourseEnrollment {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long studentId;
    
    private Long courseId;
    
    private String status; // enrolled, dropped
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
} 