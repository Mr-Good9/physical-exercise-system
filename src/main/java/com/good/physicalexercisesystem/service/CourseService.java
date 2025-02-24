package com.good.physicalexercisesystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.good.physicalexercisesystem.entity.Course;
import com.good.physicalexercisesystem.vo.CourseVO;

public interface CourseService {
    // 分页获取课程列表
    Page<CourseVO> getCourseList(String name, String type, Page<Course> page, Long studentId);
    
    // 选课
    void enrollCourse(Long studentId, Long courseId);
    
    // 退课
    void dropCourse(Long studentId, Long courseId);
} 