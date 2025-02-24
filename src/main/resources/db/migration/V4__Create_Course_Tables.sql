-- 课程表
CREATE TABLE pe_course (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '课程名称',
    teacher_id BIGINT NOT NULL COMMENT '教师ID',
    type VARCHAR(20) NOT NULL COMMENT '课程类型:required-必修,optional-选修',
    time VARCHAR(50) NOT NULL COMMENT '上课时间',
    location VARCHAR(100) NOT NULL COMMENT '上课地点',
    capacity INT NOT NULL COMMENT '课程容量',
    enrolled INT DEFAULT 0 COMMENT '已选人数',
    description TEXT COMMENT '课程描述',
    enabled TINYINT(1) DEFAULT 1 COMMENT '是否启用:1-启用,0-禁用',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    deleted TINYINT(1) DEFAULT 0 COMMENT '是否删除:1-已删除,0-未删除',
    CONSTRAINT fk_course_teacher FOREIGN KEY (teacher_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- 选课记录表
CREATE TABLE pe_course_enrollment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    status VARCHAR(20) NOT NULL COMMENT '状态:enrolled-已选,dropped-已退',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    deleted TINYINT(1) DEFAULT 0 COMMENT '是否删除:1-已删除,0-未删除',
    CONSTRAINT fk_enrollment_student FOREIGN KEY (student_id) REFERENCES sys_user(id),
    CONSTRAINT fk_enrollment_course FOREIGN KEY (course_id) REFERENCES pe_course(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='选课记录表';

-- 修改测试数据,使用实际的教师ID
INSERT INTO pe_course (name, teacher_id, type, time, location, capacity, enrolled, description, create_time, update_time)
SELECT 
    '体育基础课',
    u.id,
    'required',
    '周一 08:00-09:40',
    '操场',
    50,
    45,
    '本课程是一门基础体育课程，旨在提高学生的基本运动能力和身体素质。课程内容包括体能训练、基本运动技能等。',
    NOW(),
    NOW()
FROM sys_user u 
WHERE u.user_type = 'teacher' 
LIMIT 1;

INSERT INTO pe_course (name, teacher_id, type, time, location, capacity, enrolled, description, create_time, update_time)
SELECT 
    '篮球选修课',
    u.id,
    'optional',
    '周三 14:00-15:40',
    '篮球场',
    30,
    28,
    '本课程主要教授篮球基本技术和战术，适合对篮球有兴趣的学生选修。',
    NOW(),
    NOW()
FROM sys_user u 
WHERE u.user_type = 'teacher' 
LIMIT 1 OFFSET 1;

INSERT INTO pe_course (name, teacher_id, type, time, location, capacity, enrolled, description, create_time, update_time)
SELECT 
    '体能训练',
    u.id,
    'optional',
    '周五 10:00-11:40',
    '体育馆',
    40,
    35,
    '本课程focus在提高学生的体能水平，包括力量训练、耐力训练、灵活性训练等。',
    NOW(),
    NOW()
FROM sys_user u 
WHERE u.user_type = 'teacher' 
LIMIT 1 OFFSET 2; 