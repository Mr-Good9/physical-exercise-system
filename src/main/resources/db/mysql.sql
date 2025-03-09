CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `name` varchar(50) NOT NULL COMMENT '真实姓名',
  `user_type` varchar(20) NOT NULL COMMENT '用户类型:admin-管理员,teacher-教师,student-学生',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号码',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `gender` varchar(10) DEFAULT NULL COMMENT '性别',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '头像URL',
  `enabled` tinyint(1) DEFAULT '1' COMMENT '是否启用:1-启用,0-禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除:1-已删除,0-未删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统用户表';

CREATE TABLE `student_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '关联用户ID',
  `student_id` varchar(50) NOT NULL COMMENT '学号',
  `class_name` varchar(50) NOT NULL COMMENT '班级名称',
  `grade` varchar(20) NOT NULL COMMENT '年级',
  `major` varchar(50) DEFAULT NULL COMMENT '专业',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除:1-已删除,0-未删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `student_id` (`student_id`),
  UNIQUE KEY `uk_student_id` (`student_id`),
  KEY `fk_student_user` (`user_id`),
  CONSTRAINT `fk_student_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生信息扩展表';

CREATE TABLE `teacher_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '关联用户ID',
  `teacher_code` varchar(50) NOT NULL COMMENT '教师编号',
  `title` varchar(50) DEFAULT NULL COMMENT '职称',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除:1-已删除,0-未删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `teacher_code` (`teacher_code`),
  UNIQUE KEY `uk_teacher_code` (`teacher_code`),
  KEY `fk_teacher_user` (`user_id`),
  CONSTRAINT `fk_teacher_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='教师信息扩展表';

CREATE TABLE `teacher_course_relation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `teacher_id` bigint NOT NULL COMMENT '教师ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除:1-已删除,0-未删除',
  PRIMARY KEY (`id`),
  KEY `fk_relation_teacher` (`teacher_id`),
  KEY `fk_relation_course` (`course_id`),
  CONSTRAINT `fk_relation_course` FOREIGN KEY (`course_id`) REFERENCES `pe_course` (`id`),
  CONSTRAINT `fk_relation_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='教师-课程关系表';


CREATE TABLE `pe_class` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `class_name` varchar(50) NOT NULL COMMENT '班级名称',
  `grade` varchar(20) NOT NULL COMMENT '年级',
  `teacher_id` bigint DEFAULT NULL COMMENT '教师ID',
  `semester` varchar(20) NOT NULL COMMENT '学期',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `fk_class_teacher` (`teacher_id`),
  CONSTRAINT `fk_class_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='班级信息表';

CREATE TABLE `pe_course` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) NOT NULL COMMENT '课程名称',
  `teacher_id` bigint NOT NULL COMMENT '教师ID',
  `type` varchar(20) NOT NULL COMMENT '课程类型:required-必修,optional-选修',
  `time` varchar(50) NOT NULL COMMENT '上课时间',
  `location` varchar(100) NOT NULL COMMENT '上课地点',
  `capacity` int NOT NULL COMMENT '课程容量',
  `enrolled` int DEFAULT '0' COMMENT '已选人数',
  `description` text COMMENT '课程描述',
  `enabled` tinyint(1) DEFAULT '1' COMMENT '是否启用:1-启用,0-禁用',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除:1-已删除,0-未删除',
  `semester` varchar(20) NOT NULL DEFAULT '2025秋季' COMMENT '学期',
  `start_date` date NOT NULL DEFAULT '2024-09-01' COMMENT '开课日期',
  `end_date` date NOT NULL DEFAULT '2025-12-31' COMMENT '结课日期',
  `status` varchar(20) NOT NULL DEFAULT 'pending' COMMENT '课程状态:pending-未开始,active-进行中,ended-已结束',
  PRIMARY KEY (`id`),
  KEY `fk_course_teacher` (`teacher_id`),
  CONSTRAINT `fk_course_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='课程表';

CREATE TABLE `pe_course_attendance` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `attendance_date` date NOT NULL COMMENT '考勤日期',
  `attendance_time` time NOT NULL COMMENT '考勤时间',
  `status` varchar(20) NOT NULL COMMENT '考勤状态:present-出席,absent-缺席,late-迟到,leave-请假',
  `remark` text COMMENT '备注说明',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_attendance_record` (`course_id`,`student_id`,`attendance_date`),
  KEY `fk_attendance_student` (`student_id`),
  CONSTRAINT `fk_attendance_course` FOREIGN KEY (`course_id`) REFERENCES `pe_course` (`id`),
  CONSTRAINT `fk_attendance_student` FOREIGN KEY (`student_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='课程考勤表';

CREATE TABLE `pe_course_enrollment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `status` varchar(20) NOT NULL COMMENT '状态:enrolled-已选,dropped-已退',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除:1-已删除,0-未删除',
  PRIMARY KEY (`id`),
  KEY `fk_enrollment_student` (`student_id`),
  KEY `fk_enrollment_course` (`course_id`),
  CONSTRAINT `fk_enrollment_course` FOREIGN KEY (`course_id`) REFERENCES `pe_course` (`id`),
  CONSTRAINT `fk_enrollment_student` FOREIGN KEY (`student_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='选课记录表';

CREATE TABLE `pe_course_score` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `score` decimal(5,2) NOT NULL COMMENT '成绩分数',
  `evaluation` varchar(50) DEFAULT NULL COMMENT '评价等级',
  `teacher_comment` text COMMENT '教师评语',
  `score_type` varchar(20) NOT NULL COMMENT '成绩类型:regular-平时成绩,midterm-期中成绩,final-期末成绩',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_course_score` (`course_id`,`student_id`,`score_type`),
  KEY `fk_score_student` (`student_id`),
  CONSTRAINT `fk_score_course` FOREIGN KEY (`course_id`) REFERENCES `pe_course` (`id`),
  CONSTRAINT `fk_score_student` FOREIGN KEY (`student_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='课程成绩表';

CREATE TABLE `pe_notice` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(100) NOT NULL COMMENT '标题',
  `content` text NOT NULL COMMENT '内容',
  `type` varchar(20) NOT NULL COMMENT '通知类型',
  `enabled` tinyint NOT NULL DEFAULT '1' COMMENT '是否启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='通知公告表';

CREATE TABLE `pe_student_class` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `class_id` bigint NOT NULL COMMENT '班级ID',
  `status` varchar(20) NOT NULL DEFAULT 'active' COMMENT '状态:active-在读,graduated-毕业,transferred-转班',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_class` (`student_id`,`class_id`),
  KEY `fk_class_ref` (`class_id`),
  CONSTRAINT `fk_class_ref` FOREIGN KEY (`class_id`) REFERENCES `pe_class` (`id`),
  CONSTRAINT `fk_student_ref` FOREIGN KEY (`student_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生班级关联表';

CREATE TABLE `pe_teacher_todo` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `teacher_id` bigint NOT NULL COMMENT '教师ID',
  `title` varchar(100) NOT NULL COMMENT '待办标题',
  `content` text COMMENT '待办内容',
  `type` varchar(20) NOT NULL COMMENT '待办类型:attendance-考勤,evaluation-评价,test-体测',
  `status` varchar(20) NOT NULL DEFAULT 'pending' COMMENT '状态:pending-待处理,processing-处理中,completed-已完成',
  `due_date` datetime DEFAULT NULL COMMENT '截止时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `fk_todo_teacher` (`teacher_id`),
  CONSTRAINT `fk_todo_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='教师待办事项表';

CREATE TABLE `physical_test_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `item_name` varchar(50) NOT NULL COMMENT '项目名称',
  `item_code` varchar(50) NOT NULL COMMENT '项目代码',
  `description` text COMMENT '项目描述',
  `unit` varchar(20) DEFAULT NULL COMMENT '计量单位',
  `enabled` tinyint(1) DEFAULT '1' COMMENT '是否启用:1-启用,0-禁用',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除:1-已删除,0-未删除',
  `standard_value` json DEFAULT NULL COMMENT '标准值配置',
  `scoring_rules` json DEFAULT NULL COMMENT '评分规则',
  PRIMARY KEY (`id`),
  UNIQUE KEY `item_code` (`item_code`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='体测项目表';

CREATE TABLE `physical_test_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `test_item_id` bigint NOT NULL COMMENT '测试项目ID',
  `test_result` varchar(50) NOT NULL COMMENT '测试结果',
  `score` int NOT NULL COMMENT '得分',
  `evaluation` varchar(50) DEFAULT NULL COMMENT '评价等级',
  `teacher_comment` text COMMENT '教师评语',
  `test_date` datetime NOT NULL COMMENT '测试日期',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除:1-已删除,0-未删除',
  PRIMARY KEY (`id`),
  KEY `fk_test_student` (`student_id`),
  KEY `fk_test_item` (`test_item_id`),
  CONSTRAINT `fk_test_item` FOREIGN KEY (`test_item_id`) REFERENCES `physical_test_item` (`id`),
  CONSTRAINT `fk_test_student` FOREIGN KEY (`student_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='体测记录表';

