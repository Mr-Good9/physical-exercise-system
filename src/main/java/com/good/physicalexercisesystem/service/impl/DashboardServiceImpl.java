package com.good.physicalexercisesystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.good.physicalexercisesystem.entity.Course;
import com.good.physicalexercisesystem.entity.CourseEnrollment;
import com.good.physicalexercisesystem.entity.PhysicalTestItem;
import com.good.physicalexercisesystem.entity.PhysicalTestRecord;
import com.good.physicalexercisesystem.mapper.CourseEnrollmentMapper;
import com.good.physicalexercisesystem.mapper.CourseMapper;
import com.good.physicalexercisesystem.mapper.PhysicalTestItemMapper;
import com.good.physicalexercisesystem.mapper.PhysicalTestRecordMapper;
import com.good.physicalexercisesystem.service.DashboardService;
import com.good.physicalexercisesystem.utils.UserContext;
import com.good.physicalexercisesystem.vo.DashboardVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final CourseMapper courseMapper;
    private final CourseEnrollmentMapper enrollmentMapper;
    private final PhysicalTestRecordMapper testRecordMapper;
    private final PhysicalTestItemMapper testItemMapper;


    public DashboardServiceImpl(CourseMapper courseMapper,
                                CourseEnrollmentMapper enrollmentMapper,
                                PhysicalTestRecordMapper testRecordMapper, PhysicalTestItemMapper testItemMapper) {
        this.courseMapper = courseMapper;
        this.enrollmentMapper = enrollmentMapper;
        this.testRecordMapper = testRecordMapper;
        this.testItemMapper = testItemMapper;
    }

    @Override
    public DashboardVO getStudentDashboardStatistics() {
        DashboardVO vo = new DashboardVO();
        Long studentId = UserContext.getUser().getId();

        // 获取课程统计
        List<CourseEnrollment> enrollments = enrollmentMapper.selectList(
                new LambdaQueryWrapper<CourseEnrollment>()
                        .eq(CourseEnrollment::getStudentId, studentId)
                        .eq(CourseEnrollment::getStatus, "enrolled")
        );

        // 如果有选课记录才查询课程信息
        if (!enrollments.isEmpty()) {
            List<Long> courseIds = enrollments.stream()
                    .map(CourseEnrollment::getCourseId)
                    .collect(Collectors.toList());

            List<Course> courses = courseMapper.selectList(
                    new LambdaQueryWrapper<Course>()
                            .in(Course::getId, courseIds)
            );

            vo.setTotalCourses(courses.size());
            vo.setRequiredCourses((int) courses.stream()
                    .filter(c -> "required".equals(c.getType()))
                    .count());
            vo.setOptionalCourses((int) courses.stream()
                    .filter(c -> "optional".equals(c.getType()))
                    .count());
        } else {
            vo.setTotalCourses(0);
            vo.setRequiredCourses(0);
            vo.setOptionalCourses(0);
        }

        // 获取体测统计
        List<PhysicalTestRecord> records = testRecordMapper.selectList(
                new LambdaQueryWrapper<PhysicalTestRecord>()
                        .eq(PhysicalTestRecord::getStudentId, studentId)
                        .orderByDesc(PhysicalTestRecord::getTestDate)
        );

        if (!records.isEmpty()) {
            PhysicalTestRecord latest = records.get(0);
            vo.setLatestScore(Double.valueOf(latest.getScore()));

            // 获取最新测试项目名称
            PhysicalTestItem testItem = testItemMapper.selectById(latest.getTestItemId());
            if (testItem != null) {
                vo.setLatestTestType(testItem.getItemName());
            }

            // 计算平均分和通过率
            double totalScore = records.stream()
                    .mapToDouble(PhysicalTestRecord::getScore)
                    .sum();
            vo.setAverageScore(totalScore / records.size());
            vo.setTotalTests(records.size());

            long passedCount = records.stream()
                    .filter(r -> r.getScore() >= 60)
                    .count();
            vo.setPassedTests((int) passedCount);
            vo.setPassRate(passedCount * 100.0 / records.size());

            // 计算趋势
            if (records.size() > 1) {
                double previousScore = records.get(1).getScore();
                vo.setMonthOverMonth((latest.getScore() - previousScore) / previousScore * 100);

                if (records.size() > 2) {
                    double lastYearScore = records.get(2).getScore();
                    vo.setYearOverYear((latest.getScore() - lastYearScore) / lastYearScore * 100);
                }
            }
        } else {
            // 设置默认值
            vo.setLatestScore(0.0);
            vo.setLatestTestType("");
            vo.setAverageScore(0.0);
            vo.setTotalTests(0);
            vo.setPassedTests(0);
            vo.setPassRate(0.0);
            vo.setMonthOverMonth(0.0);
            vo.setYearOverYear(0.0);
        }
        return vo;
    }
}
