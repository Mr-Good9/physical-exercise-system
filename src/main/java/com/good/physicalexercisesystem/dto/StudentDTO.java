package com.good.physicalexercisesystem.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Data
public class StudentDTO {
    private Long id;
    
    @NotBlank(message = "学号不能为空")
    @Pattern(regexp = "^\\d{8}$", message = "学号必须为8位数字")
    private String studentId;
    
    @NotBlank(message = "姓名不能为空")
    private String name;
    
    @NotBlank(message = "班级不能为空")
    private String className;
    
    @NotBlank(message = "年级不能为空")
    private String grade;
    
    private String major;
    
    @NotBlank(message = "性别不能为空")
    private String gender;
    
    private String phone;
    
    private String email;
    
    private String avatar;
    
    private Double attendance;
    
    private Double physicalScore;
    
    private String status;
    
    private Boolean enabled;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
} 