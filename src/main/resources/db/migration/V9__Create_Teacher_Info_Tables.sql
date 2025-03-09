-- 教师信息表
CREATE TABLE teacher_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '关联用户ID',
    teacher_code VARCHAR(50) NOT NULL UNIQUE COMMENT '教师编号',
    title VARCHAR(50) COMMENT '职称',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '是否删除:1-已删除,0-未删除',
    CONSTRAINT fk_teacher_user FOREIGN KEY (user_id) REFERENCES sys_user(id),
    CONSTRAINT uk_teacher_code UNIQUE (teacher_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师信息扩展表';

-- 教师-课程关系表
CREATE TABLE teacher_course_relation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    teacher_id BIGINT NOT NULL COMMENT '教师ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '是否删除:1-已删除,0-未删除',
    CONSTRAINT fk_relation_teacher FOREIGN KEY (teacher_id) REFERENCES sys_user(id),
    CONSTRAINT fk_relation_course FOREIGN KEY (course_id) REFERENCES pe_course(id),
    CONSTRAINT uk_teacher_course_semester UNIQUE (teacher_id, course_id, role_type, semester)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师-课程关系表';


