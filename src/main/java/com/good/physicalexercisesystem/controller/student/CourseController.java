package com.good.physicalexercisesystem.controller.student;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.good.physicalexercisesystem.common.ApiResponse;
import com.good.physicalexercisesystem.entity.Course;
import com.good.physicalexercisesystem.entity.User;
import com.good.physicalexercisesystem.service.CourseService;
import com.good.physicalexercisesystem.service.UserService;
import com.good.physicalexercisesystem.vo.CourseVO;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/student/course")
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;

    public CourseController(CourseService courseService, UserService userService) {
        this.courseService = courseService;
        this.userService = userService;
    }

    @GetMapping("/list")
    public ApiResponse<Page<CourseVO>> getCourseList(
            Authentication authentication,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        User user = userService.findByUsername(authentication.getName());
        Page<Course> page = new Page<>(current, size);
        return ApiResponse.success(courseService.getCourseList(name, type, page, user.getId()));
    }

    @PostMapping("/{courseId}/enroll")
    public ApiResponse<Void> enrollCourse(
            Authentication authentication,
            @PathVariable Long courseId
    ) {
        User user = userService.findByUsername(authentication.getName());
        courseService.enrollCourse(user.getId(), courseId);
        return ApiResponse.success("选课成功", null);
    }

    @PostMapping("/{courseId}/drop")
    public ApiResponse<Void> dropCourse(
            Authentication authentication,
            @PathVariable Long courseId
    ) {
        User user = userService.findByUsername(authentication.getName());
        courseService.dropCourse(user.getId(), courseId);
        return ApiResponse.success("退课成功", null);
    }
} 