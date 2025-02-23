-- 创建数据库
CREATE DATABASE `physical_exercise` CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_0900_ai_ci';

-- 用户表
CREATE TABLE sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(50) NOT NULL,
    user_type VARCHAR(20) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    gender VARCHAR(10),
    enabled BOOLEAN DEFAULT TRUE,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    deleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT uk_username UNIQUE (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 初始化管理员账号
-- 密码是123456
INSERT INTO sys_user (username, password, name, user_type, enabled, create_time, update_time)
VALUES ('admin', '$2a$10$X/hX4qvWTWt8V3.eGJHhzO8GWQJsRlhz5jyXgNy.FiNnVU8e9pBJi', '管理员', 'admin', true, NOW(), NOW()); 