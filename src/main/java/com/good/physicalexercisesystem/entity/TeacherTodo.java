package com.good.physicalexercisesystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("pe_teacher_todo")
public class TeacherTodo {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 教师ID
     */
    private Long teacherId;

    /**
     * 待办标题
     */
    private String title;

    /**
     * 待办内容
     */
    private String content;

    /**
     * 待办类型：attendance-考勤,evaluation-评价,test-体测
     */
    private String type;

    /**
     * 状态：pending-待处理,processing-处理中,completed-已完成
     */
    private String status;

    /**
     * 截止时间
     */
    private LocalDateTime dueDate;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Boolean deleted;
} 