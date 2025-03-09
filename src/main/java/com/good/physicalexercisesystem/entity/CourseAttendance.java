package com.good.physicalexercisesystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("pe_course_attendance")
public class CourseAttendance {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 课程id
     */
    private Long courseId;
    /**
     * 学生id
     */
    private Long studentId;
    /**
     * 考勤日期
     */
    private LocalDate attendanceDate;
    /**
     * 考勤状态:present-出席,absent-缺席,late-迟到,leave-请假
     */
    private String status;
    /**
     * 备注说明
     */
    private String remark;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean deleted;
}
