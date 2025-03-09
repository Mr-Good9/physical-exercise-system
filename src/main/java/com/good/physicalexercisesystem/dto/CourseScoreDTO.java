package com.good.physicalexercisesystem.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.DecimalMax;
import java.math.BigDecimal;

@Data
public class CourseScoreDTO {
    /**
     * 学生ID
     */
    @NotNull(message = "学生ID不能为空")
    private Long studentId;
    
    /**
     * 成绩分数
     */
    @NotNull(message = "成绩分数不能为空")
    @DecimalMin(value = "0.0", message = "成绩分数不能小于0")
    @DecimalMax(value = "100.0", message = "成绩分数不能大于100")
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
     * 成绩类型
     */
    @NotBlank(message = "成绩类型不能为空")
    private String scoreType;
} 