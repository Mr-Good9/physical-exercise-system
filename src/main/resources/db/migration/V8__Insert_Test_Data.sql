-- 插入测试用户数据 (密码都是123456的加密值)
INSERT INTO sys_user (username, password, name, user_type, phone, email, gender, enabled, create_time, update_time) 
VALUES 
-- 管理员
('admin', '$2a$10$X/hX4qvWTWt8V3.eGJHhzO8GWQJsRlhz5jyXgNy.FiNnVU8e9pBJi', '系统管理员', 'admin', '13800000001', 'admin@example.com', 'male', 1, NOW(), NOW()),

-- 教师
('teacher01', '$2a$10$X/hX4qvWTWt8V3.eGJHhzO8GWQJsRlhz5jyXgNy.FiNnVU8e9pBJi', '张教练', 'teacher', '13800000002', 'teacher01@example.com', 'male', 1, NOW(), NOW()),
('teacher02', '$2a$10$X/hX4qvWTWt8V3.eGJHhzO8GWQJsRlhz5jyXgNy.FiNnVU8e9pBJi', '李教练', 'teacher', '13800000003', 'teacher02@example.com', 'female', 1, NOW(), NOW()),

-- 学生
('20230001', '$2a$10$X/hX4qvWTWt8V3.eGJHhzO8GWQJsRlhz5jyXgNy.FiNnVU8e9pBJi', '张三', 'student', '13800000004', 'student01@example.com', 'male', 1, NOW(), NOW()),
('20230002', '$2a$10$X/hX4qvWTWt8V3.eGJHhzO8GWQJsRlhz5jyXgNy.FiNnVU8e9pBJi', '李四', 'student', '13800000005', 'student02@example.com', 'male', 1, NOW(), NOW()),
('20230003', '$2a$10$X/hX4qvWTWt8V3.eGJHhzO8GWQJsRlhz5jyXgNy.FiNnVU8e9pBJi', '王五', 'student', '13800000006', 'student03@example.com', 'male', 1, NOW(), NOW()),
('20230004', '$2a$10$X/hX4qvWTWt8V3.eGJHhzO8GWQJsRlhz5jyXgNy.FiNnVU8e9pBJi', '赵六', 'student', '13800000007', 'student04@example.com', 'female', 1, NOW(), NOW()),
('20230005', '$2a$10$X/hX4qvWTWt8V3.eGJHhzO8GWQJsRlhz5jyXgNy.FiNnVU8e9pBJi', '小红', 'student', '13800000008', 'student05@example.com', 'female', 1, NOW(), NOW());

-- 插入学生信息
INSERT INTO student_info (user_id, student_id, class_name, grade, major, create_time, update_time) 
SELECT id, username, 
    CASE 
        WHEN id % 2 = 0 THEN '计算机科学1班'
        ELSE '计算机科学2班'
    END,
    '2023级',
    '计算机科学与技术',
    NOW(), 
    NOW()
FROM sys_user 
WHERE user_type = 'student';

-- 插入体测项目
INSERT INTO physical_test_item (item_name, item_code, description, unit, create_time, update_time) 
VALUES 
('50米跑', '50m_run', '测试短跑速度素质', '秒', NOW(), NOW()),
('立定跳远', 'standing_long_jump', '测试下肢爆发力', '厘米', NOW(), NOW()),
('坐位体前屈', 'sit_and_reach', '测试柔韧素质', '厘米', NOW(), NOW()),
('1000米跑(男)/800米跑(女)', 'long_distance_run', '测试耐力素质', '秒', NOW(), NOW()),
('引体向上(男)/仰卧起坐(女)', 'strength_test', '测试力量素质', '个', NOW(), NOW());

-- 插入体测记录
INSERT INTO physical_test_record (student_id, test_item_id, test_result, score, evaluation, test_date, create_time, update_time)
SELECT 
    u.id as student_id,
    i.id as test_item_id,
    CASE i.item_code
        WHEN '50m_run' THEN CONCAT(FLOOR(6 + RAND() * 2), '.', FLOOR(RAND() * 99))
        WHEN 'standing_long_jump' THEN CONCAT(FLOOR(200 + RAND() * 50))
        WHEN 'sit_and_reach' THEN CONCAT(FLOOR(5 + RAND() * 20))
        WHEN 'long_distance_run' THEN CONCAT(FLOOR(200 + RAND() * 100))
        WHEN 'strength_test' THEN CONCAT(FLOOR(5 + RAND() * 15))
    END as test_result,
    FLOOR(60 + RAND() * 40) as score,
    CASE 
        WHEN RAND() > 0.7 THEN '优秀'
        WHEN RAND() > 0.4 THEN '良好'
        ELSE '及格'
    END as evaluation,
    DATE_SUB(CURDATE(), INTERVAL FLOOR(RAND() * 30) DAY) as test_date,
    NOW() as create_time,
    NOW() as update_time
FROM sys_user u
CROSS JOIN physical_test_item i
WHERE u.user_type = 'student';

-- 插入考勤记录
INSERT INTO attendance_record (student_id, course_id, attendance_date, attendance_time, status, create_time, update_time)
SELECT 
    u.id as student_id,
    c.id as course_id,
    DATE_SUB(CURDATE(), INTERVAL seq.n DAY) as attendance_date,
    CASE 
        WHEN seq.n % 2 = 0 THEN '08:00:00'
        ELSE '14:00:00'
    END as attendance_time,
    CASE 
        WHEN RAND() > 0.9 THEN 'absent'
        WHEN RAND() > 0.8 THEN 'late'
        WHEN RAND() > 0.7 THEN 'leave'
        ELSE 'present'
    END as status,
    NOW() as create_time,
    NOW() as update_time
FROM sys_user u
CROSS JOIN pe_course c
CROSS JOIN (
    SELECT a.N + b.N * 10 + 1 n
    FROM (SELECT 0 AS N UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) a
    ,(SELECT 0 AS N UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4) b
    ORDER BY n
) seq
WHERE u.user_type = 'student' 
AND seq.n <= 30; 