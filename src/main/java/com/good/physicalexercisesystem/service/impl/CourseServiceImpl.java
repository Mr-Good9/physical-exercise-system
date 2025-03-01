package com.good.physicalexercisesystem.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.BeanUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.good.physicalexercisesystem.entity.Course;
import com.good.physicalexercisesystem.entity.CourseEnrollment;
import com.good.physicalexercisesystem.entity.User;
import com.good.physicalexercisesystem.mapper.CourseEnrollmentMapper;
import com.good.physicalexercisesystem.mapper.CourseMapper;
import com.good.physicalexercisesystem.mapper.UserMapper;
import com.good.physicalexercisesystem.service.CourseService;
import com.good.physicalexercisesystem.utils.UserContext;
import com.good.physicalexercisesystem.vo.CourseVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseMapper courseMapper;
    private final CourseEnrollmentMapper enrollmentMapper;
    private final UserMapper userMapper;

    public CourseServiceImpl(CourseMapper courseMapper,
                             CourseEnrollmentMapper enrollmentMapper,
                             UserMapper userMapper) {
        this.courseMapper = courseMapper;
        this.enrollmentMapper = enrollmentMapper;
        this.userMapper = userMapper;
    }

    @Override
    public Page<CourseVO> getCourseList(String name, String type, Page<Course> page) {

        // 模糊查询课程信息
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<Course>()
                .like(name != null && !name.isEmpty(), Course::getName, name)
                .eq(type != null && !type.isEmpty(), Course::getType, type)
                .eq(Course::getEnabled, 1)
                .orderByDesc(Course::getCreateTime);

        Page<Course> coursePage = courseMapper.selectPage(page, wrapper);
        if (CollectionUtils.isEmpty(coursePage.getRecords())) {
            return new Page<>(page.getCurrent(), page.getSize(), 0L); // 返回一个空的分页对象
        }
        // 获取教师信息
        List<Long> teacherIds = coursePage.getRecords().stream()
                .map(Course::getTeacherId)
                .collect(Collectors.toList());

        Map<Long, String> teacherNames = userMapper.selectList(
                new LambdaQueryWrapper<User>()
                        .in(User::getId, teacherIds)
        ).stream().collect(Collectors.toMap(
                User::getId,
                User::getName
        ));

        // 查询学生已选课程
        List<Long> enrolledCourseIds = enrollmentMapper.selectList(
                        new LambdaQueryWrapper<CourseEnrollment>()
                                .eq(CourseEnrollment::getStudentId, UserContext.getUser().getId())
                                .eq(CourseEnrollment::getStatus, "enrolled")
                ).stream()
                .map(CourseEnrollment::getCourseId)
                .collect(Collectors.toList());

        // 转换为VO
        Page<CourseVO> voPage = new Page<>(
                coursePage.getCurrent(),
                coursePage.getSize(),
                coursePage.getTotal()
        );
        // 构造返回结果
        List<CourseVO> voList = coursePage.getRecords().stream()
                .map(course -> {
                    CourseVO vo = new CourseVO();
                    vo.setId(course.getId());
                    vo.setName(course.getName());
                    vo.setTeacherId(course.getTeacherId());
                    vo.setTeacherName(teacherNames.get(course.getTeacherId()));
                    vo.setType(course.getType());
                    vo.setTime(course.getTime());
                    vo.setLocation(course.getLocation());
                    vo.setCapacity(course.getCapacity());
                    vo.setEnrolled(course.getEnrolled());
                    vo.setDescription(course.getDescription());
                    vo.setIsEnrolled(enrolledCourseIds.contains(course.getId()));
                    return vo;
                })
                .collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    @Transactional
    public void enrollCourse(Long studentId, Long courseId) {
        // 检查课程是否存在且可选
        Course course = courseMapper.selectById(courseId);
        if (course == null || course.getEnabled() != 1) {
            throw new RuntimeException("课程不存在或已关闭");
        }

        // 检查是否已满
        if (course.getEnrolled() >= course.getCapacity()) {
            throw new RuntimeException("课程已满");
        }

        // 检查是否已选
        Long count = enrollmentMapper.selectCount(
                new LambdaQueryWrapper<CourseEnrollment>()
                        .eq(CourseEnrollment::getStudentId, studentId)
                        .eq(CourseEnrollment::getCourseId, courseId)
                        .eq(CourseEnrollment::getStatus, "enrolled")
        );
        if (count > 0) {
            throw new RuntimeException("已选择该课程");
        }

        // 创建选课记录
        CourseEnrollment enrollment = new CourseEnrollment();
        enrollment.setStudentId(studentId);
        enrollment.setCourseId(courseId);
        enrollment.setStatus("enrolled");
        enrollmentMapper.insert(enrollment);

        // 更新课程已选人数
        course.setEnrolled(course.getEnrolled() + 1);
        courseMapper.updateById(course);
    }

    @Override
    @Transactional
    public void dropCourse(Long studentId, Long courseId) {
        // 检查选课记录
        CourseEnrollment enrollment = enrollmentMapper.selectOne(
                new LambdaQueryWrapper<CourseEnrollment>()
                        .eq(CourseEnrollment::getStudentId, studentId)
                        .eq(CourseEnrollment::getCourseId, courseId)
                        .eq(CourseEnrollment::getStatus, "enrolled")
        );
        if (enrollment == null) {
            throw new RuntimeException("未选择该课程");
        }

        // 更新选课状态
        enrollment.setStatus("dropped");
        enrollmentMapper.updateById(enrollment);

        // 更新课程已选人数
        Course course = courseMapper.selectById(courseId);
        course.setEnrolled(course.getEnrolled() - 1);
        courseMapper.updateById(course);
    }

    @Override
    public List<Course> getRecentCourses(Long studentId) {
        // 还要联查到teacherName
        List<Course> courses = courseMapper.selectList(
                new LambdaQueryWrapper<Course>()
                        .inSql(Course::getId,
                                "SELECT course_id FROM pe_course_enrollment WHERE student_id = " + studentId +
                                " AND status = 'enrolled' AND deleted = 0")
                        .orderByDesc(Course::getCreateTime)
                        .last("LIMIT 5")
        );
        // 获取教师信息
        List<Long> teacherIds = courses.stream()
                .map(Course::getTeacherId)
                .collect(Collectors.toList());

        Map<Long, String> teacherNames = userMapper.selectList(
                new LambdaQueryWrapper<User>()
                        .in(User::getId, teacherIds)
        ).stream().collect(Collectors.toMap(
                User::getId,
                User::getName
        ));
        for (Course course : courses) {
            course.setTeacherName(teacherNames.get(course.getTeacherId()));
        }
        return courses;
    }

    @Override
    public Map<String, Integer> getStudentCourseStatistics(Long studentId) {
        Map<String, Integer> statistics = new HashMap<>();

        // 获取学生已选课程
        List<Course> courses = courseMapper.selectList(
                new LambdaQueryWrapper<Course>()
                        .inSql(Course::getId,
                                "SELECT course_id FROM pe_course_enrollment WHERE student_id = " + studentId +
                                " AND status = 'enrolled' AND deleted = 0")
        );

        // 统计课程数量
        statistics.put("total", courses.size());
        statistics.put("required", (int) courses.stream()
                .filter(c -> "required".equals(c.getType()))
                .count());
        statistics.put("optional", (int) courses.stream()
                .filter(c -> "optional".equals(c.getType()))
                .count());

        return statistics;
    }
}
