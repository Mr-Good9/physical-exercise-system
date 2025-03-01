CREATE TABLE pe_notice (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL COMMENT '标题',
    content TEXT NOT NULL COMMENT '内容',
    type VARCHAR(20) NOT NULL COMMENT '通知类型',
    enabled TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除'
) COMMENT '通知公告表';

-- 插入一些测试数据
INSERT INTO pe_notice (title, content, type) VALUES
('体育课程调整通知', '由于场地维护，本周三的篮球课程调整到室内体育馆进行', 'course'),
('体测安排通知', '本学期体测将于下周开始，请同学们提前做好准备', 'test'),
('运动会报名通知', '校运动会报名工作现已开始，请有意向参加的同学尽快报名', 'competition'),
('体育馆开放时间调整', '因疫情防控需要，体育馆开放时间调整为每日9:00-17:00', 'facility'),
('优秀运动员表彰', '祝贺我校在省运动会上取得优异成绩的运动员们', 'announcement'); 