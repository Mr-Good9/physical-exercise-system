package com.good.physicalexercisesystem.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.good.physicalexercisesystem.dto.CourseAttendanceDTO;
import com.good.physicalexercisesystem.dto.CourseDTO;
import com.good.physicalexercisesystem.dto.CourseScoreDTO;
import com.good.physicalexercisesystem.entity.*;
import com.good.physicalexercisesystem.mapper.*;
import com.good.physicalexercisesystem.service.CourseService;
import com.good.physicalexercisesystem.utils.UserContext;
import com.good.physicalexercisesystem.vo.CourseAttendanceVO;
import com.good.physicalexercisesystem.vo.CourseQuery;
import com.good.physicalexercisesystem.vo.CourseScoreVO;
import com.good.physicalexercisesystem.vo.CourseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private CourseEnrollmentMapper enrollmentMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CourseAttendanceMapper attendanceMapper;
    @Autowired
    private CourseScoreMapper scoreMapper;

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
        if (course == null || !course.getEnabled()) {
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


    @Override
    public IPage<CourseVO> getCourseList(CourseQuery query, Page<CourseVO> page) {
        return courseMapper.selectCourseList(page, query);
    }

    @Override
    public CourseVO createCourse(CourseDTO courseDTO) {
        Course course = new Course();
        BeanUtil.copyProperties(courseDTO, course);
        course.setTeacherId(UserContext.getUser().getId());
        course.setStatus("pending");
        course.setEnabled(true);
        course.setEnrolled(0);
        courseMapper.insert(course);
        return convertToVO(course);
    }

    @Override
    public CourseVO updateCourse(Long id, CourseDTO courseDTO) {
        Course course = courseMapper.selectById(id);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }
        BeanUtil.copyProperties(courseDTO, course);
        courseMapper.updateById(course);
        return convertToVO(course);
    }

    @Override
    public void deleteCourse(Long id) {
        Course course = courseMapper.selectById(id);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }
        courseMapper.deleteById(id);
    }

    @Override
    public List<CourseAttendanceVO> getCourseAttendance(Long courseId) {
        return attendanceMapper.selectAttendanceList(courseId);
    }

    @Override
    @Transactional
    public void saveCourseAttendance(Long courseId, List<CourseAttendanceDTO> attendanceList) {
        for (CourseAttendanceDTO dto : attendanceList) {
            CourseAttendance attendance = new CourseAttendance();
            attendance.setCourseId(courseId);
            BeanUtil.copyProperties(dto, attendance);
            attendanceMapper.insert(attendance);
        }
    }

    @Override
    public List<CourseScoreVO> getCourseScores(Long courseId) {
        return scoreMapper.selectScoreList(courseId);
    }

    @Override
    @Transactional
    public void saveCourseScores(Long courseId, List<CourseScoreDTO> scoreList) {
        for (CourseScoreDTO dto : scoreList) {
            CourseScore score = new CourseScore();
            score.setCourseId(courseId);
            BeanUtil.copyProperties(dto, score);
            scoreMapper.insert(score);
        }
    }

    private CourseVO convertToVO(Course course) {
        if (course == null) {
            return null;
        }
        CourseVO vo = new CourseVO();
        BeanUtil.copyProperties(course, vo);
        // 获取教师名称
        if (course.getTeacherId() != null) {
            User teacher = userMapper.selectById(course.getTeacherId());
            if (teacher != null) {
                vo.setTeacherName(teacher.getName());
            }
        }
        return vo;
    }
}
