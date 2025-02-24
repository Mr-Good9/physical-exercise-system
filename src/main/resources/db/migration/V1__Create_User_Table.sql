-- 创建数据库
CREATE DATABASE `physical_exercise` CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_0900_ai_ci';

-- 用户表
CREATE TABLE sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    user_type VARCHAR(20) NOT NULL COMMENT '用户类型:admin-管理员,teacher-教师,student-学生',
    phone VARCHAR(20) COMMENT '手机号码',
    email VARCHAR(100) COMMENT '邮箱',
    gender VARCHAR(10) COMMENT '性别',
    enabled TINYINT(1) DEFAULT 1 COMMENT '是否启用:1-启用,0-禁用',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    deleted TINYINT(1) DEFAULT 0 COMMENT '是否删除:1-已删除,0-未删除',
    CONSTRAINT uk_username UNIQUE (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 初始化管理员账号
-- 密码是123456
INSERT INTO sys_user (username, password, name, user_type, enabled, create_time, update_time)
VALUES ('admin', '$2a$10$X/hX4qvWTWt8V3.eGJHhzO8GWQJsRlhz5jyXgNy.FiNnVU8e9pBJi', '管理员', 'admin', 1, NOW(), NOW()); 