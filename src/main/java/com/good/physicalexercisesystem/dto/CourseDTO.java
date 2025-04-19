package com.good.physicalexercisesystem.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CourseDTO {
    private Long id;

    @NotBlank(message = "课程名称不能为空")
    @JsonAlias("courseName")
    private String name;

    private Long teacherId;

    @NotBlank(message = "课程类型不能为空")
    @JsonAlias("courseType")
    private String type;

    private String weekday;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String time;

    private String schedule;

    @NotBlank(message = "上课地点不能为空")
    private String location;

    @NotNull(message = "课程容量不能为空")
    @Min(value = 1, message = "课程容量必须大于0")
    private Integer capacity;

    private Integer enrolled;

    private String description;

    private Boolean enabled;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Boolean deleted;

    private String semester;

    private LocalDate startDate;

    private LocalDate endDate;

    private String status;

    // 用于设置默认值的方法
    public void setDefaultValues() {
        if (enabled == null) enabled = true;
        if (enrolled == null) enrolled = 0;
        if (deleted == null) deleted = false;
        if (status == null || status.isEmpty()) status = "pending";
        if (semester == null) semester = "2025春季";
        if (startDate == null && startTime != null) startDate = startTime.toLocalDate();
        if (endDate == null && endTime != null) endDate = endTime.toLocalDate();
        if (createTime == null) createTime = LocalDateTime.now();
        if (updateTime == null) updateTime = LocalDateTime.now();
    }
}
