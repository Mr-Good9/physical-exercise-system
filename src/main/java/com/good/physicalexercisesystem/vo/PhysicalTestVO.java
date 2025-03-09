package com.good.physicalexercisesystem.vo;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class PhysicalTestVO {
    private Long id;
    private String name;
    private String type;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalStudents;
    private Integer completedCount;
    private String status;
    private List<TestItemVO> items;
    private List<ClassVO> classes;
    private String description;
} 