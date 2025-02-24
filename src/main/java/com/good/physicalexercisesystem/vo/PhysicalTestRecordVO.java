package com.good.physicalexercisesystem.vo;

import com.good.physicalexercisesystem.entity.PhysicalTestRecord;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PhysicalTestRecordVO {
    private Long id;
    private Long studentId;
    private Long testItemId;
    private String testResult;
    private Integer score;
    private String evaluation;
    private String teacherComment;
    private LocalDateTime testDate;
    
    // 项目信息
    private String itemName;
    private String itemCode;
    private String unit;
    
    public static PhysicalTestRecordVO from(PhysicalTestRecord record) {
        PhysicalTestRecordVO vo = new PhysicalTestRecordVO();
        vo.setId(record.getId());
        vo.setStudentId(record.getStudentId());
        vo.setTestItemId(record.getTestItemId());
        vo.setTestResult(record.getTestResult());
        vo.setScore(record.getScore());
        vo.setEvaluation(record.getEvaluation());
        vo.setTeacherComment(record.getTeacherComment());
        vo.setTestDate(record.getTestDate());
        return vo;
    }
} 