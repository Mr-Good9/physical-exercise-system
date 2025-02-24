package com.good.physicalexercisesystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("physical_test_item")
public class PhysicalTestItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String itemName;
    
    private String itemCode;
    
    private String description;
    
    private String unit;
    
    private Integer enabled;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
} 