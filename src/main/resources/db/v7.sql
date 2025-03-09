-- V7__Create_Teacher_Management_Tables.sql

-- 1. 教师课程表扩展
-- 为DATE类型列指定有效默认值，并添加所有新列
ALTER TABLE pe_course 
ADD COLUMN semester VARCHAR(20) NOT NULL DEFAULT '2025秋季' COMMENT '学期',
ADD COLUMN start_date DATE NOT NULL DEFAULT '2024-09-01' COMMENT '开课日期',
ADD COLUMN end_date DATE NOT NULL DEFAULT '2025-12-31' COMMENT '结课日期',
ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '课程状态:pending-未开始,active-进行中,ended-已结束';

-- 2. 课程考勤表
CREATE TABLE pe_course_attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    attendance_date DATE NOT NULL COMMENT '考勤日期',
    status VARCHAR(20) NOT NULL COMMENT '考勤状态:present-出席,absent-缺席,late-迟到,leave-请假',
    remark TEXT COMMENT '备注说明',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    CONSTRAINT fk_attendance_course FOREIGN KEY (course_id) REFERENCES pe_course(id),
    CONSTRAINT fk_attendance_student FOREIGN KEY (student_id) REFERENCES sys_user(id),
    CONSTRAINT uk_attendance_record UNIQUE (course_id, student_id, attendance_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程考勤表';

-- 3. 课程成绩表
CREATE TABLE pe_course_score (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    score DECIMAL(5,2) NOT NULL COMMENT '成绩分数',
    evaluation VARCHAR(50) COMMENT '评价等级',
    teacher_comment TEXT COMMENT '教师评语',
    score_type VARCHAR(20) NOT NULL COMMENT '成绩类型:regular-平时成绩,midterm-期中成绩,final-期末成绩',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    CONSTRAINT fk_score_course FOREIGN KEY (course_id) REFERENCES pe_course(id),
    CONSTRAINT fk_score_student FOREIGN KEY (student_id) REFERENCES sys_user(id),
    CONSTRAINT uk_course_score UNIQUE (course_id, student_id, score_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程成绩表';

-- 4. 体测项目表扩展
ALTER TABLE physical_test_item
ADD COLUMN standard_value JSON COMMENT '标准值配置',
ADD COLUMN scoring_rules JSON COMMENT '评分规则';

-- 5. 教师待办事项表
CREATE TABLE pe_teacher_todo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    teacher_id BIGINT NOT NULL COMMENT '教师ID',
    title VARCHAR(100) NOT NULL COMMENT '待办标题',
    content TEXT COMMENT '待办内容',
    type VARCHAR(20) NOT NULL COMMENT '待办类型:attendance-考勤,evaluation-评价,test-体测',
    status VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态:pending-待处理,processing-处理中,completed-已完成',
    due_date DATETIME COMMENT '截止时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    CONSTRAINT fk_todo_teacher FOREIGN KEY (teacher_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师待办事项表';

-- 6. 班级信息表
CREATE TABLE pe_class (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    class_name VARCHAR(50) NOT NULL COMMENT '班级名称',
    grade VARCHAR(20) NOT NULL COMMENT '年级',
    teacher_id BIGINT COMMENT '班主任ID',
    semester VARCHAR(20) NOT NULL COMMENT '学期',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    CONSTRAINT fk_class_teacher FOREIGN KEY (teacher_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级信息表';

-- 7. 学生班级关联表
CREATE TABLE pe_student_class (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    class_id BIGINT NOT NULL COMMENT '班级ID',
    status VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '状态:active-在读,graduated-毕业,transferred-转班',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    CONSTRAINT fk_student_ref FOREIGN KEY (student_id) REFERENCES sys_user(id),
    CONSTRAINT fk_class_ref FOREIGN KEY (class_id) REFERENCES pe_class(id),
    CONSTRAINT uk_student_class UNIQUE (student_id, class_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生班级关联表';

