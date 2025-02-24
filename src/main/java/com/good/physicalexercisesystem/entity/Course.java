package com.good.physicalexercisesystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("pe_course")
public class Course {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private Long teacherId;
    
    @TableField(exist = false)
    private String teacherName;
    
    private String type; // required, optional
    
    private String time;
    
    private String location;
    
    private Integer capacity;
    
    private Integer enrolled;
    
    private String description;
    
    private Integer enabled;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
} 