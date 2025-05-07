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
        // 获取当前登录教师ID
        Long teacherId = UserContext.getUser().getId();
        if (teacherId == null) {
            throw new RuntimeException("未获取到当前登录教师信息");
        }
        TeacherDashboardVO teacherDashboardVO = new TeacherDashboardVO();

        // 获取学生统计
        TeacherDashboardVO.StudentStats studentStats = new TeacherDashboardVO.StudentStats();
        // 计算学生总数
        Integer totalStudents = countTotalStudents(teacherId);
        studentStats.setTotal(totalStudents);
        // 计算新学生数
        Integer newStudents = countNewStudents(teacherId);
        studentStats.setNewCount(newStudents);

        // 添加活跃学生统计
        Integer activeStudents = countActiveStudents(teacherId);
        studentStats.setActive(activeStudents);
        teacherDashboardVO.setStudentStats(studentStats);

        // 获取课程统计
        TeacherDashboardVO.CourseStats courseStats = new TeacherDashboardVO.CourseStats();
        Integer totalCourses = countTotalCourses(teacherId);
        courseStats.setTotal(totalCourses);
        Integer activeCourses = countActiveCourses(teacherId);
        courseStats.setActive(activeCourses);
        Integer endedCourses = countEndedCourses(teacherId);
        courseStats.setEnded(endedCourses);
        teacherDashboardVO.setCourseStats(courseStats);

        // 添加周统计数据（如果前端需要）
        TeacherDashboardVO.WeekStats weekStats = new TeacherDashboardVO.WeekStats();
        weekStats.setHours(0); // 填充默认值或实际计算值
        weekStats.setCompleted(0);
        weekStats.setUpcoming(0);
        teacherDashboardVO.setWeekStats(weekStats);

        return teacherDashboardVO;
    }

    @Override
    public List<TeacherTodoVO> getTodos() {
        Long teacherId = UserContext.getUser().getId();
        if (teacherId == null) {
            throw new RuntimeException("未获取到当前登录教师信息");
        }
        return dashboardMapper.selectTeacherTodos(teacherId);
    }

    @Override
    public List<CourseVO> getRecentCourses() {
        Long teacherId = UserContext.getUser().getId();
        if (teacherId == null) {
            throw new RuntimeException("未获取到当前登录教师信息");
        }
        return dashboardMapper.selectRecentCourses(teacherId);
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
        Long currentTeacherId = UserContext.getUser().getId();
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
        Long currentTeacherId = UserContext.getUser().getId();
        if (!todo.getTeacherId().equals(currentTeacherId)) {
            throw new RuntimeException("无权操作此待办事项");
        }

        todoMapper.deleteById(id);
    }

    /**
     * 统计教师管理的班级中的总学生数
     */
    private Integer countTotalStudents(Long teacherId) {
        return Math.toIntExact(userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUserType, "student")
                .eq(User::getDeleted, false)
                .inSql(User::getId,
                        "SELECT DISTINCT student_id FROM pe_student_class WHERE class_id IN " +
                        "(SELECT id FROM pe_class WHERE teacher_id = " + teacherId + " AND deleted = 0) " +
                        "AND status = 'active' AND deleted = 0")));
    }

    /**
     * 统计本月新增学生数（加入教师管理的班级）
     */
    private Integer countNewStudents(Long teacherId) {
        LocalDateTime startOfMonth = LocalDateTime.of(
                LocalDate.now().withDayOfMonth(1),
                LocalTime.MIN
        );

        return Math.toIntExact(userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUserType, "student")
                .eq(User::getDeleted, false)
                .ge(User::getCreateTime, startOfMonth)
                .inSql(User::getId,
                        "SELECT DISTINCT student_id FROM pe_student_class WHERE class_id IN " +
                        "(SELECT id FROM pe_class WHERE teacher_id = " + teacherId + " AND deleted = 0) " +
                        "AND status = 'active' AND deleted = 0")));
    }

    /**
     * 统计活跃学生数（最近一周有更新记录的学生）
     */
    private Integer countActiveStudents(Long teacherId) {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minus(7, ChronoUnit.DAYS);

        return Math.toIntExact(userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUserType, "student")
                .eq(User::getDeleted, false)
                .ge(User::getUpdateTime, oneWeekAgo)
                .inSql(User::getId,
                        "SELECT DISTINCT student_id FROM pe_student_class WHERE class_id IN " +
                        "(SELECT id FROM pe_class WHERE teacher_id = " + teacherId + " AND deleted = 0) " +
                        "AND status = 'active' AND deleted = 0")));
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
     * 使用start_date和end_date来判断课程状态
     */
    private Integer countActiveCourses(Long teacherId) {
        LocalDate today = LocalDate.now();
        return Math.toIntExact(courseMapper.selectCount(new LambdaQueryWrapper<Course>()
                .eq(Course::getTeacherId, teacherId)
                .le(Course::getStartDate, today)  // 开始日期小于等于今天
                .ge(Course::getEndDate, today)    // 结束日期大于等于今天
                .eq(Course::getEnabled, true)
                .eq(Course::getDeleted, false)));
    }

    /**
     * 统计已结束的课程数
     * 使用end_date来判断课程是否已结束
     */
    private Integer countEndedCourses(Long teacherId) {
        LocalDate today = LocalDate.now();
        return Math.toIntExact(courseMapper.selectCount(new LambdaQueryWrapper<Course>()
                .eq(Course::getTeacherId, teacherId)
                .lt(Course::getEndDate, today)    // 结束日期小于今天
                .eq(Course::getEnabled, true)
                .eq(Course::getDeleted, false)));
    }

    /**
     * 统计未开始的课程数
     * 使用start_date来判断课程是否未开始
     */
    private Integer countPendingCourses(Long teacherId) {
        LocalDate today = LocalDate.now();
        return Math.toIntExact(courseMapper.selectCount(new LambdaQueryWrapper<Course>()
                .eq(Course::getTeacherId, teacherId)
                .gt(Course::getStartDate, today)  // 开始日期大于今天
                .eq(Course::getEnabled, true)
                .eq(Course::getDeleted, false)));
    }

}
