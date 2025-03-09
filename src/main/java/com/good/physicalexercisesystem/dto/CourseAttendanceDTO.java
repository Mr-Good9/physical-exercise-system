package com.good.physicalexercisesystem.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
public class CourseAttendanceDTO {
    /**
     * 学生ID
     */
    @NotNull(message = "学生ID不能为空")
    private Long studentId;
    
    /**
     * 考勤日期
     */
    @NotNull(message = "考勤日期不能为空")
    private LocalDate attendanceDate;
    
    /**
     * 考勤状态
     */
    @NotBlank(message = "考勤状态不能为空")
    private String status;
    
    /**
     * 备注说明
     */
    private String remark;
} 