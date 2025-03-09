package com.good.physicalexercisesystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@TableName("pe_course")
public class Course {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 课程名称
     */
    private String name;
    
    /**
     * 教师ID
     */
    private Long teacherId;
    
    /**
     * 教师姓名
     */
    @TableField(exist = false)
    private String teacherName;
    
    /**
     * 课程类型：required-必修,optional-选修
     */
    private String type;
    
    /**
     * 上课时间
     */
    private String time;
    
    /**
     * 上课地点
     */
    private String location;
    
    /**
     * 课程容量
     */
    private Integer capacity;
    
    /**
     * 已选人数
     */
    private Integer enrolled;
    
    /**
     * 课程描述
     */
    private String description;
    
    /**
     * 学期
     */
    private String semester;
    
    /**
     * 开课日期
     */
    private LocalDate startDate;
    
    /**
     * 结课日期
     */
    private LocalDate endDate;
    
    /**
     * 课程状态：pending-未开始,active-进行中,ended-已结束
     */
    private String status;
    
    /**
     * 是否启用
     */
    private Boolean enabled;
    
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