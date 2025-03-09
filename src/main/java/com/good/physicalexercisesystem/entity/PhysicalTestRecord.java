package com.good.physicalexercisesystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("physical_test_record")
public class PhysicalTestRecord {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 学生id
     */
    private Long studentId;
    /**
     * 测试项目id
     */
    private Long testItemId;
    /**
     * 测试结果
     */
    private String testResult;
    /**
     * 得分
     */
    private Integer score;
    /**
     * 评价等级
     */
    private String evaluation;
    /**
     * 教师评价
     */
    private String teacherComment;
    /**
     * 测试日期
     */
    private LocalDateTime testDate;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    /**
     * 是否删除:1-已删除,0-未删除
     */
    @TableLogic
    private Integer deleted;
}
