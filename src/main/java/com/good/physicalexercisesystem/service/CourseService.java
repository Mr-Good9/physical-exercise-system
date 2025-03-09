package com.good.physicalexercisesystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.good.physicalexercisesystem.dto.CourseDTO;
import com.good.physicalexercisesystem.dto.CourseAttendanceDTO;
import com.good.physicalexercisesystem.dto.CourseScoreDTO;
import com.good.physicalexercisesystem.entity.Course;
import com.good.physicalexercisesystem.vo.CourseAttendanceVO;
import com.good.physicalexercisesystem.vo.CourseQuery;
import com.good.physicalexercisesystem.vo.CourseScoreVO;
import com.good.physicalexercisesystem.vo.CourseVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface CourseService {
    /**
     * 分页获取课程列表(学生端)
     */
    Page<CourseVO> getCourseList(String name, String type, Page<Course> page);

    /**
     * 选课
     */
    void enrollCourse(Long studentId, Long courseId);

    /**
     * 退课
     */
    void dropCourse(Long studentId, Long courseId);

    /**
     * 获取学生近期课程
     */
    List<Course> getRecentCourses(Long studentId);

    /**
     * 获取学生课程统计数据
     */
    Map<String, Integer> getStudentCourseStatistics(Long studentId);

    /**
     * 分页获取课程列表(教师端)
     */
    IPage<CourseVO> getCourseList(CourseQuery query, Page<CourseVO> page);

    /**
     * 创建课程
     */
    CourseVO createCourse(CourseDTO courseDTO);

    /**
     * 更新课程
     */
    CourseVO updateCourse(Long id, CourseDTO courseDTO);

    /**
     * 删除课程
     */
    void deleteCourse(Long id);

    /**
     * 获取课程考勤列表
     */
    List<CourseAttendanceVO> getCourseAttendance(Long courseId);

    /**
     * 保存课程考勤记录
     */
    void saveCourseAttendance(Long courseId, List<CourseAttendanceDTO> attendanceList);

    /**
     * 获取课程成绩列表
     */
    List<CourseScoreVO> getCourseScores(Long courseId);

    /**
     * 保存课程成绩记录
     */
    void saveCourseScores(Long courseId, List<CourseScoreDTO> scoreList);
}
