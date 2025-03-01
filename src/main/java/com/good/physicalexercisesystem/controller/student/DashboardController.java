package com.good.physicalexercisesystem.controller.student;

import com.good.physicalexercisesystem.common.CommonResult;
import com.good.physicalexercisesystem.entity.User;
import com.good.physicalexercisesystem.service.CourseService;
import com.good.physicalexercisesystem.service.PhysicalTestService;
import com.good.physicalexercisesystem.service.UserService;
import com.good.physicalexercisesystem.vo.DashboardVO;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/student/dashboard")
public class DashboardController {

    private final UserService userService;
    private final CourseService courseService;
    private final PhysicalTestService physicalTestService;

    public DashboardController(UserService userService,
                             CourseService courseService,
                             PhysicalTestService physicalTestService) {
        this.userService = userService;
        this.courseService = courseService;
        this.physicalTestService = physicalTestService;
    }

    @GetMapping("/statistics")
    public CommonResult<DashboardVO> getStatistics(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        DashboardVO vo = new DashboardVO();

        // 获取课程统计
        Map<String, Integer> courseStats = courseService.getStudentCourseStatistics(user.getId());
        vo.setTotalCourses(courseStats.getOrDefault("total", 0));
        vo.setRequiredCourses(courseStats.getOrDefault("required", 0));
        vo.setOptionalCourses(courseStats.getOrDefault("optional", 0));

        // 获取体测统计
        Map<String, Object> testStats = physicalTestService.getStudentTestStatistics(user.getId());
        vo.setLatestScore(Double.valueOf(testStats.getOrDefault("latestScore", 0.0).toString()));
        vo.setLatestTestType(testStats.getOrDefault("latestTestType", "").toString());
        vo.setAverageScore(Double.valueOf(testStats.getOrDefault("averageScore", 0.0).toString()));
        vo.setTotalTests(Integer.valueOf(testStats.getOrDefault("totalTests", 0).toString()));
        vo.setPassedTests(Integer.valueOf(testStats.getOrDefault("passedTests", 0).toString()));
        
        // 计算通过率
        if (vo.getTotalTests() > 0) {
            vo.setPassRate(vo.getPassedTests() * 100.0 / vo.getTotalTests());
        } else {
            vo.setPassRate(0.0);
        }

        return CommonResult.success(vo);
    }
} 