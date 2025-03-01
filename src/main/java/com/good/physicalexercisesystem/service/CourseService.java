package com.good.physicalexercisesystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.good.physicalexercisesystem.entity.Course;
import com.good.physicalexercisesystem.vo.CourseVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface CourseService {
    // 分页获取课程列表
    Page<CourseVO> getCourseList(String name, String type, Page<Course> page);

    // 选课
    void enrollCourse(Long studentId, Long courseId);

    // 退课
    void dropCourse(Long studentId, Long courseId);

    List<Course> getRecentCourses(Long studentId);

    /**
     * 获取学生课程统计数据
     * @param studentId 学生ID
     * @return 统计数据
     */
    Map<String, Integer> getStudentCourseStatistics(Long studentId);
}
