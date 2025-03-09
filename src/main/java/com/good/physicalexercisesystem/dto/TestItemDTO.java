package com.good.physicalexercisesystem.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class TestItemDTO {
    /**
     * 项目名称
     */
    @NotBlank(message = "项目名称不能为空")
    private String name;
    
    /**
     * 项目代码
     */
    @NotBlank(message = "项目代码不能为空")
    private String code;
    
    /**
     * 项目类型
     */
    @NotBlank(message = "项目类型不能为空")
    private String type;
    
    /**
     * 计量单位
     */
    @NotBlank(message = "计量单位不能为空")
    private String unit;
    
    /**
     * 标准值配置
     */
    private String standardValue;
    
    /**
     * 评分规则
     */
    private String scoringRules;
    
    /**
     * 项目说明
     */
    private String description;
} 