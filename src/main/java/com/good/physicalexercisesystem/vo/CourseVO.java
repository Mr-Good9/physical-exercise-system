package com.good.physicalexercisesystem.vo;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CourseVO {
    private Long id;
    private String name;
    private Long teacherId;
    private String teacherName;
    private String type;
    private String time;
    private String location;
    private Integer capacity;
    private Integer enrolled;
    private String description;
    private String semester;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    /**
     * 是否已报名
     */
    private Boolean isEnrolled;
}
