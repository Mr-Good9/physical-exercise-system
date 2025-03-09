package com.good.physicalexercisesystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.good.physicalexercisesystem.entity.*;
import com.good.physicalexercisesystem.mapper.*;
import com.good.physicalexercisesystem.service.TeacherDashboardService;
import com.good.physicalexercisesystem.utils.UserContext;
import com.good.physicalexercisesystem.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class TeacherDashboardServiceImpl implements TeacherDashboardService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private TeacherTodoMapper todoMapper;
    @Autowired
    private TeacherDashboardMapper dashboardMapper;

    @Override
    public TeacherDashboardVO getStatistics() {
        Long teacherId = UserContext.getCurrentUserId();
        TeacherDashboardVO vo = new TeacherDashboardVO();

        // 获取学生统计
        TeacherDashboardVO.StudentStats studentStats = new TeacherDashboardVO.StudentStats();
        studentStats.setTotal(countTotalStudents(teacherId));
        studentStats.setNewCount(countNewStudents(teacherId));
        studentStats.setActive(countActiveStudents(teacherId));
        vo.setStudentStats(studentStats);

        // 获取课程统计
        TeacherDashboardVO.CourseStats courseStats = new TeacherDashboardVO.CourseStats();
        courseStats.setTotal(countTotalCourses(teacherId));
        courseStats.setActive(countActiveCourses(teacherId));
        courseStats.setEnded(countEndedCourses(teacherId));
        vo.setCourseStats(courseStats);

        // 获取本周统计
        Map<String, Object> weekStats = dashboardMapper.selectWeekCourseStats(teacherId);
        TeacherDashboardVO.WeekStats weekStatsVO = new TeacherDashboardVO.WeekStats();
        weekStatsVO.setHours(Integer.valueOf(weekStats.get("totalHours").toString()));
        weekStatsVO.setCompleted(Integer.valueOf(weekStats.get("completedHours").toString()));
        weekStatsVO.setUpcoming(Integer.valueOf(weekStats.get("upcomingHours").toString()));
        vo.setWeekStats(weekStatsVO);

        // 获取待办统计
        TeacherDashboardVO.TodoStats todoStats = new TeacherDashboardVO.TodoStats();
        todoStats.setTotal(countTotalTodos(teacherId));
        todoStats.setAttendance(countAttendanceTodos(teacherId));
        todoStats.setEvaluation(countEvaluationTodos(teacherId));
        vo.setTodoStats(todoStats);

        return vo;
    }

    @Override
    public List<CourseVO> getRecentCourses() {
        Long teacherId = UserContext.getCurrentUserId();
        return dashboardMapper.selectRecentCourses(teacherId);
    }

    @Override
    public List<TeacherTodoVO> getTodos() {
        Long teacherId = UserContext.getCurrentUserId();
        return dashboardMapper.selectTeacherTodos(teacherId);
    }

    @Override
    public void updateTodoStatus(Long id, String status) {
        TeacherTodo todo = todoMapper.selectById(id);
        if (todo == null) {
            throw new RuntimeException("待办事项不存在");
        }

        // 检查状态是否合法
        if (!Arrays.asList("pending", "processing", "completed").contains(status)) {
            throw new RuntimeException("无效的状态值");
        }

        // 检查是否是当前教师的待办事项
        Long currentTeacherId = UserContext.getCurrentUserId();
        if (!todo.getTeacherId().equals(currentTeacherId)) {
            throw new RuntimeException("无权操作此待办事项");
        }

        todo.setStatus(status);
        todoMapper.updateById(todo);
    }

    @Override
    public void deleteTodo(Long id) {
        TeacherTodo todo = todoMapper.selectById(id);
        if (todo == null) {
            throw new RuntimeException("待办事项不存在");
        }

        // 检查是否是当前教师的待办事项
        Long currentTeacherId = UserContext.getCurrentUserId();
        if (!todo.getTeacherId().equals(currentTeacherId)) {
            throw new RuntimeException("无权操作此待办事项");
        }

        todoMapper.deleteById(id);
    }

    /**
     * 统计教师的总学生数
     */
    private Integer countTotalStudents(Long teacherId) {
        return Math.toIntExact(userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUserType, "student")
                .inSql(User::getId,
                        "SELECT DISTINCT student_id FROM pe_course_enrollment WHERE course_id IN " +
                        "(SELECT id FROM pe_course WHERE teacher_id = " + teacherId + " AND deleted = 0) " +
                        "AND status = 'enrolled' AND deleted = 0")
                .eq(User::getDeleted, false)));
    }

    /**
     * 统计本月新增学生数
     */
    private Integer countNewStudents(Long teacherId) {
        LocalDateTime startOfMonth = LocalDateTime.of(
                LocalDate.now().withDayOfMonth(1),
                LocalTime.MIN
        );
        return Math.toIntExact(userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUserType, "student")
                .inSql(User::getId,
                        "SELECT DISTINCT student_id FROM pe_course_enrollment WHERE course_id IN " +
                        "(SELECT id FROM pe_course WHERE teacher_id = " + teacherId + " AND deleted = 0) " +
                        "AND status = 'enrolled' AND deleted = 0")
                .ge(User::getCreateTime, startOfMonth)
                .eq(User::getDeleted, false)));
    }

    /**
     * 统计活跃学生数（最近一周有考勤记录的学生）
     */
    private Integer countActiveStudents(Long teacherId) {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minus(7, ChronoUnit.DAYS);
        return Math.toIntExact(userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUserType, "student")
                .inSql(User::getId,
                        "SELECT DISTINCT student_id FROM pe_course_attendance WHERE course_id IN " +
                        "(SELECT id FROM pe_course WHERE teacher_id = " + teacherId + " AND deleted = 0) " +
                        "AND attendance_date >= '" + oneWeekAgo + "' AND deleted = 0")
                .eq(User::getDeleted, false)));
    }

    /**
     * 统计教师的总课程数
     */
    private Integer countTotalCourses(Long teacherId) {
        return Math.toIntExact(courseMapper.selectCount(new LambdaQueryWrapper<Course>()
                .eq(Course::getTeacherId, teacherId)
                .eq(Course::getDeleted, false)));
    }

    /**
     * 统计进行中的课程数
     */
    private Integer countActiveCourses(Long teacherId) {
        return Math.toIntExact(courseMapper.selectCount(new LambdaQueryWrapper<Course>()
                .eq(Course::getTeacherId, teacherId)
                .eq(Course::getStatus, "active")
                .eq(Course::getDeleted, false)));
    }

    /**
     * 统计已结束的课程数
     */
    private Integer countEndedCourses(Long teacherId) {
        return Math.toIntExact(courseMapper.selectCount(new LambdaQueryWrapper<Course>()
                .eq(Course::getTeacherId, teacherId)
                .eq(Course::getStatus, "ended")
                .eq(Course::getDeleted, false)));
    }

    /**
     * 统计总待办数
     */
    private Integer countTotalTodos(Long teacherId) {
        return Math.toIntExact(todoMapper.selectCount(new LambdaQueryWrapper<TeacherTodo>()
                .eq(TeacherTodo::getTeacherId, teacherId)
                .ne(TeacherTodo::getStatus, "completed")
                .eq(TeacherTodo::getDeleted, false)));
    }

    /**
     * 统计考勤待办数
     */
    private Integer countAttendanceTodos(Long teacherId) {
        return Math.toIntExact(todoMapper.selectCount(new LambdaQueryWrapper<TeacherTodo>()
                .eq(TeacherTodo::getTeacherId, teacherId)
                .eq(TeacherTodo::getType, "attendance")
                .ne(TeacherTodo::getStatus, "completed")
                .eq(TeacherTodo::getDeleted, false)));
    }

    /**
     * 统计评价待办数
     */
    private Integer countEvaluationTodos(Long teacherId) {
        return Math.toIntExact(todoMapper.selectCount(new LambdaQueryWrapper<TeacherTodo>()
                .eq(TeacherTodo::getTeacherId, teacherId)
                .eq(TeacherTodo::getType, "evaluation")
                .ne(TeacherTodo::getStatus, "completed")
                .eq(TeacherTodo::getDeleted, false)));
    }
}
