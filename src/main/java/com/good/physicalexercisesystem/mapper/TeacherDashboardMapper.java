package com.good.physicalexercisesystem.mapper;

import com.good.physicalexercisesystem.vo.CourseVO;
import com.good.physicalexercisesystem.vo.TeacherTodoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface TeacherDashboardMapper {
    /**
     * 查询教师近期课程
     */
    List<CourseVO> selectRecentCourses(@Param("teacherId") Long teacherId);

    /**
     * 查询本周课程统计
     */
    Map<String, Object> selectWeekCourseStats(@Param("teacherId") Long teacherId);

    /**
     * 查询教师待办事项
     */
    List<TeacherTodoVO> selectTeacherTodos(@Param("teacherId") Long teacherId);
} 