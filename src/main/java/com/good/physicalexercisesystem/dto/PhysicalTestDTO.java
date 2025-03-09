package com.good.physicalexercisesystem.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
public class PhysicalTestDTO {
    /**
     * 测试名称
     */
    @NotBlank(message = "测试名称不能为空")
    private String name;
    
    /**
     * 测试类型
     */
    @NotBlank(message = "测试类型不能为空")
    private String type;
    
    /**
     * 测试时间范围
     */
    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;
    
    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;
    
    /**
     * 测试项目ID列表
     */
    @NotEmpty(message = "测试项目不能为空")
    private List<Long> itemIds;
    
    /**
     * 测试班级ID列表
     */
    @NotEmpty(message = "测试班级不能为空")
    private List<Long> classIds;
    
    /**
     * 备注说明
     */
    private String description;
} 