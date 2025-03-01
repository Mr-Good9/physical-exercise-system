package com.good.physicalexercisesystem.controller.student;

import com.good.physicalexercisesystem.common.CommonResult;
import com.good.physicalexercisesystem.entity.Course;
import com.good.physicalexercisesystem.entity.PhysicalTestRecord;
import com.good.physicalexercisesystem.entity.User;
import com.good.physicalexercisesystem.dto.StudentInfo;
import com.good.physicalexercisesystem.entity.Notice;
import com.good.physicalexercisesystem.service.CourseService;
import com.good.physicalexercisesystem.service.PhysicalTestService;
import com.good.physicalexercisesystem.service.UserService;
import com.good.physicalexercisesystem.service.NoticeService;
import com.good.physicalexercisesystem.vo.PhysicalTestRecordVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {

    private final UserService userService;
    private final CourseService courseService;
    private final PhysicalTestService physicalTestService;
    private final NoticeService noticeService;

    @Autowired
    public StudentController(UserService userService,
                           CourseService courseService,
                           PhysicalTestService physicalTestService,
                           NoticeService noticeService) {
        this.userService = userService;
        this.courseService = courseService;
        this.physicalTestService = physicalTestService;
        this.noticeService = noticeService;
    }

    @GetMapping("/info")
    public CommonResult<StudentInfo> getStudentInfo(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        StudentInfo info = new StudentInfo();
        // 填充学生信息
        BeanUtils.copyProperties(user, info);
        return CommonResult.success(info);
    }

    /**
     * 获取最近课程
     * @param authentication
     * @return
     */
    @GetMapping("/courses/recent")
    public CommonResult<List<Course>> getRecentCourses(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        List<Course> courses = courseService.getRecentCourses(user.getId());
        return CommonResult.success(courses);
    }

    /**
     * 获取10条最新的学生测试记录
     * @param authentication
     * @return
     */
    @GetMapping("/physical-tests")
    public CommonResult<List<PhysicalTestRecordVO>> getPhysicalTests(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        List<PhysicalTestRecordVO> tests = physicalTestService.getStudentTests(user.getId());
        return CommonResult.success(tests);
    }

    /**
     * 获取通知
     * @return
     */
    @GetMapping("/notices")
    public CommonResult<List<Notice>> getNotices() {
        List<Notice> notices = noticeService.getRecentNotices();
        return CommonResult.success(notices);
    }
}
