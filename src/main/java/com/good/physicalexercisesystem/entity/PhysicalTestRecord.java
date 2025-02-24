package com.good.physicalexercisesystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("physical_test_record")
public class PhysicalTestRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long studentId;
    
    private Long testItemId;
    
    private String testResult;
    
    private Integer score;
    
    private String evaluation;
    
    private String teacherComment;
    
    private LocalDateTime testDate;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
} 