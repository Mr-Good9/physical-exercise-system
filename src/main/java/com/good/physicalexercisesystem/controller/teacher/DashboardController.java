package com.good.physicalexercisesystem.controller.teacher;

import com.good.physicalexercisesystem.common.CommonResult;
import com.good.physicalexercisesystem.service.TeacherDashboardService;
import com.good.physicalexercisesystem.vo.CourseVO;
import com.good.physicalexercisesystem.vo.TeacherDashboardVO;
import com.good.physicalexercisesystem.vo.TeacherTodoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("TeacherDashboardController")
@RequestMapping("/teacher/dashboard")
@Api(tags = "教师仪表盘接口")
public class DashboardController {

    @Autowired
    private TeacherDashboardService dashboardService;

    @GetMapping("/statistics")
    @ApiOperation("获取仪表盘统计数据")
    public CommonResult<TeacherDashboardVO> getStatistics() {
        return CommonResult.success(dashboardService.getStatistics());
    }

    @GetMapping("/recent-courses")
    @ApiOperation("获取近期课程")
    public CommonResult<List<CourseVO>> getRecentCourses() {
        return CommonResult.success(dashboardService.getRecentCourses());
    }

    @GetMapping("/todos")
    @ApiOperation("获取待办事项")
    public CommonResult<List<TeacherTodoVO>> getTodos() {
        return CommonResult.success(dashboardService.getTodos());
    }

    @PutMapping("/todos/{id}/status")
    @ApiOperation("更新待办事项状态")
    public CommonResult<Void> updateTodoStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        dashboardService.updateTodoStatus(id, status);
        return CommonResult.success(null);
    }

    @DeleteMapping("/todos/{id}")
    @ApiOperation("删除待办事项")
    public CommonResult<Void> deleteTodo(@PathVariable Long id) {
        dashboardService.deleteTodo(id);
        return CommonResult.success(null);
    }
}
