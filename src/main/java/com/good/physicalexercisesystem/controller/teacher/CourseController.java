package com.good.physicalexercisesystem.controller.teacher;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.good.physicalexercisesystem.common.CommonResult;
import com.good.physicalexercisesystem.dto.CourseAttendanceDTO;
import com.good.physicalexercisesystem.dto.CourseDTO;
import com.good.physicalexercisesystem.dto.CourseScoreDTO;
import com.good.physicalexercisesystem.service.CourseService;
import com.good.physicalexercisesystem.vo.CourseQuery;
import com.good.physicalexercisesystem.vo.CourseVO;
import com.good.physicalexercisesystem.vo.CourseAttendanceVO;
import com.good.physicalexercisesystem.vo.CourseScoreVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController("TeacherCourseController")
@RequestMapping("/teacher/courses")
@Api(tags = "教师课程管理接口")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @GetMapping
    @ApiOperation("获取课程列表")
    public CommonResult<IPage<CourseVO>> getCourseList(CourseQuery query, Page<CourseVO> page) {
        return CommonResult.success(courseService.getCourseList(query, page));
    }

    @PostMapping
    @ApiOperation("创建课程")
    public CommonResult<CourseVO> createCourse(@RequestBody @Valid CourseDTO courseDTO) {
        return CommonResult.success(courseService.createCourse(courseDTO));
    }

    @PutMapping("/{id}")
    @ApiOperation("更新课程")
    public CommonResult<CourseVO> updateCourse(@PathVariable Long id, @RequestBody @Valid CourseDTO courseDTO) {
        return CommonResult.success(courseService.updateCourse(id, courseDTO));
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除课程")
    public CommonResult<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return CommonResult.success(null);
    }

    @GetMapping("/{id}/attendance")
    @ApiOperation("获取课程考勤列表")
    public CommonResult<List<CourseAttendanceVO>> getCourseAttendance(@PathVariable Long id) {
        return CommonResult.success(courseService.getCourseAttendance(id));
    }

    @PostMapping("/{id}/attendance")
    @ApiOperation("保存课程考勤记录")
    public CommonResult<Void> saveCourseAttendance(
            @PathVariable Long id,
            @RequestBody @Valid List<CourseAttendanceDTO> attendanceList) {
        courseService.saveCourseAttendance(id, attendanceList);
        return CommonResult.success(null);
    }

    @GetMapping("/{id}/scores")
    @ApiOperation("获取课程成绩列表")
    public CommonResult<List<CourseScoreVO>> getCourseScores(@PathVariable Long id) {
        return CommonResult.success(courseService.getCourseScores(id));
    }

    @PostMapping("/{id}/scores")
    @ApiOperation("保存课程成绩记录")
    public CommonResult<Void> saveCourseScores(
            @PathVariable Long id,
            @RequestBody @Valid List<CourseScoreDTO> scoreList) {
        courseService.saveCourseScores(id, scoreList);
        return CommonResult.success(null);
    }
}
