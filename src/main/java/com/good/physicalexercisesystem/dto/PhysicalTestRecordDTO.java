package com.good.physicalexercisesystem.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PhysicalTestRecordDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private String className;
    private Long testItemId;
    private String testItemName;
    private String testResult;
    private Integer score;
    private String evaluation;
    private String teacherComment;
    private LocalDateTime testDate;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 