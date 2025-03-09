package com.good.physicalexercisesystem.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PhysicalGradeDTO {
    private String item;
    private String standard;
    private Integer score;
    private String level;
    private String evaluation;
    private LocalDateTime testDate;
}