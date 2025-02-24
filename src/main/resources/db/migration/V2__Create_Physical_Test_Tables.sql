-- 体测项目表
CREATE TABLE physical_test_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    item_name VARCHAR(50) NOT NULL COMMENT '项目名称',
    item_code VARCHAR(50) NOT NULL UNIQUE COMMENT '项目代码',
    description TEXT COMMENT '项目描述',
    unit VARCHAR(20) COMMENT '计量单位',
    enabled TINYINT(1) DEFAULT 1 COMMENT '是否启用:1-启用,0-禁用',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    deleted TINYINT(1) DEFAULT 0 COMMENT '是否删除:1-已删除,0-未删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='体测项目表';

-- 体测记录表
CREATE TABLE physical_test_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    test_item_id BIGINT NOT NULL COMMENT '测试项目ID',
    test_result VARCHAR(50) NOT NULL COMMENT '测试结果',
    score INT NOT NULL COMMENT '得分',
    evaluation VARCHAR(50) COMMENT '评价等级',
    teacher_comment TEXT COMMENT '教师评语',
    test_date DATETIME NOT NULL COMMENT '测试日期',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    deleted TINYINT(1) DEFAULT 0 COMMENT '是否删除:1-已删除,0-未删除',
    CONSTRAINT fk_test_student FOREIGN KEY (student_id) REFERENCES sys_user(id),
    CONSTRAINT fk_test_item FOREIGN KEY (test_item_id) REFERENCES physical_test_item(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='体测记录表';

-- 初始化体测项目数据
INSERT INTO physical_test_item (item_name, item_code, description, unit, create_time, update_time) VALUES
('50米跑', '50m_run', '测试短跑速度素质', '秒', NOW(), NOW()),
('立定跳远', 'standing_long_jump', '测试下肢爆发力', '厘米', NOW(), NOW()),
('坐位体前屈', 'sit_and_reach', '测试柔韧素质', '厘米', NOW(), NOW()),
('1000米跑(男)/800米跑(女)', 'long_distance_run', '测试耐力素质', '秒', NOW(), NOW()),
('引体向上(男)/仰卧起坐(女)', 'strength_test', '测试力量素质', '个', NOW(), NOW()),
('肺活量', 'vital_capacity', '测试呼吸系统机能', '毫升', NOW(), NOW()); 