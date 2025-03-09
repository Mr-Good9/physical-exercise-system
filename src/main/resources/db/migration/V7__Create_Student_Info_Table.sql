-- 学生信息扩展表
CREATE TABLE student_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '关联用户ID',
    student_id VARCHAR(50) NOT NULL UNIQUE COMMENT '学号',
    class_name VARCHAR(50) NOT NULL COMMENT '班级名称',
    grade VARCHAR(20) NOT NULL COMMENT '年级',
    major VARCHAR(50) COMMENT '专业',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '是否删除:1-已删除,0-未删除',
    CONSTRAINT fk_student_user FOREIGN KEY (user_id) REFERENCES sys_user(id),
    CONSTRAINT uk_student_id UNIQUE (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生信息扩展表';

-- 考勤记录表
CREATE TABLE attendance_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    attendance_date DATE NOT NULL COMMENT '考勤日期',
    attendance_time TIME NOT NULL COMMENT '考勤时间',
    status VARCHAR(20) NOT NULL COMMENT '考勤状态:present-出勤,late-迟到,leave-请假,absent-缺勤',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '是否删除:1-已删除,0-未删除',
    CONSTRAINT fk_attendance_student FOREIGN KEY (student_id) REFERENCES sys_user(id),
    CONSTRAINT fk_attendance_course FOREIGN KEY (course_id) REFERENCES pe_course(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考勤记录表'; 