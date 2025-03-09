package com.good.physicalexercisesystem.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class CourseDTO {
    @NotBlank(message = "课程名称不能为空")
    private String name;

    @NotBlank(message = "课程类型不能为空")
    private String type;

    @NotBlank(message = "上课时间不能为空")
    private String time;

    @NotBlank(message = "上课地点不能为空")
    private String location;

    @NotNull(message = "课程容量不能为空")
    @Min(value = 1, message = "课程容量必须大于0")
    private Integer capacity;

    private String description;

    @NotBlank(message = "学期不能为空")
    private String semester;

    @NotNull(message = "开课日期不能为空")
    private LocalDate startDate;

    @NotNull(message = "结课日期不能为空")
    private LocalDate endDate;
}
