package com.good.physicalexercisesystem.service;

import com.good.physicalexercisesystem.vo.TeacherDashboardVO;
import com.good.physicalexercisesystem.vo.TeacherTodoVO;
import com.good.physicalexercisesystem.vo.CourseVO;
import java.util.List;

public interface TeacherDashboardService {
    /**
     * 获取教师仪表盘统计数据
     */
    TeacherDashboardVO getStatistics();
    
    /**
     * 获取近期课程
     */
    List<CourseVO> getRecentCourses();
    
    /**
     * 获取待办事项
     */
    List<TeacherTodoVO> getTodos();

    /**
     * 更新待办事项状态
     */
    void updateTodoStatus(Long id, String status);

    /**
     * 删除待办事项
     */
    void deleteTodo(Long id);
} 