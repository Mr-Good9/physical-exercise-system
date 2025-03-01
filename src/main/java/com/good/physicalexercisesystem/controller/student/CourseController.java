package com.good.physicalexercisesystem.controller.student;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.good.physicalexercisesystem.common.CommonResult;
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

    /**
     * 获取课程列表
     *
     * @param name
     * @param type
     * @param current
     * @param size
     * @return
     */
    @GetMapping("/list")
    public CommonResult<Page<CourseVO>> getCourseList(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        Page<Course> page = new Page<>(current, size);
        Page<CourseVO> result = courseService.getCourseList(name, type, page);
        // 即使没有数据也返回成功,只是 records 为空数组
        return CommonResult.success(result);
    }

    /**
     * 选课
     *
     * @param authentication
     * @param courseId
     * @return
     */
    @PostMapping("/{courseId}/enroll")
    public CommonResult<Void> enrollCourse(
            Authentication authentication,
            @PathVariable Long courseId
    ) {
        User user = userService.findByUsername(authentication.getName());
        courseService.enrollCourse(user.getId(), courseId);
        return CommonResult.success("选课成功", null);
    }

    /**
     * 退课
     * @param authentication
     * @param courseId
     * @return
     */
    @PostMapping("/{courseId}/drop")
    public CommonResult<Void> dropCourse(
            Authentication authentication,
            @PathVariable Long courseId
    ) {
        User user = userService.findByUsername(authentication.getName());
        courseService.dropCourse(user.getId(), courseId);
        return CommonResult.success("退课成功", null);
    }
}
