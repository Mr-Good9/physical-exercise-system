package com.good.physicalexercisesystem.controller.teacher;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.good.physicalexercisesystem.common.CommonResult;
import com.good.physicalexercisesystem.dto.StudentDTO;
import com.good.physicalexercisesystem.dto.PhysicalGradeDTO;
import com.good.physicalexercisesystem.entity.AttendanceRecord;
import com.good.physicalexercisesystem.service.StudentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Api(tags = "学生管理接口")
@RestController("teacherStudentController")
@RequestMapping("/teacher/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @ApiOperation("获取学生列表")
    @GetMapping
    public CommonResult<Page<StudentDTO>> getStudentList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String className) {
        return CommonResult.success(studentService.getStudentList(page, pageSize, name, className));
    }

    @ApiOperation("添加学生")
    @PostMapping
    public CommonResult<Void> addStudent(@Validated @RequestBody StudentDTO studentDTO) {
        studentService.addStudent(studentDTO);
        return CommonResult.success(null);
    }

    @ApiOperation("更新学生信息")
    @PutMapping("/{id}")
    public CommonResult<Void> updateStudent(
            @PathVariable Long id,
            @Validated @RequestBody StudentDTO studentDTO) {
        studentService.updateStudent(id, studentDTO);
        return CommonResult.success(null);
    }

    @ApiOperation("删除学生")
    @DeleteMapping("/{id}")
    public CommonResult<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return CommonResult.success(null);
    }

    @ApiOperation("导入学生")
    @PostMapping("/import")
    public CommonResult<Void> importStudents(@RequestParam("file") MultipartFile file) {
        studentService.importStudents(file);
        return CommonResult.success(null);
    }

    @ApiOperation("获取学生体测成绩")
    @GetMapping("/{studentId}/physical-grades")
    public CommonResult<List<PhysicalGradeDTO>> getPhysicalGrades(@PathVariable Long studentId) {
        return CommonResult.success(studentService.getPhysicalGrades(studentId));
    }

    @ApiOperation("保存学生体测成绩")
    @PostMapping("/{studentId}/physical-grades")
    public CommonResult<Void> savePhysicalGrades(
            @PathVariable Long studentId,
            @Validated @RequestBody List<PhysicalGradeDTO> grades) {
        studentService.savePhysicalGrades(studentId, grades);
        return CommonResult.success(null);
    }

    @ApiOperation("获取学生考勤记录")
    @GetMapping("/{studentId}/attendance")
    public CommonResult<List<AttendanceRecord>> getAttendanceRecords(@PathVariable Long studentId) {
        return CommonResult.success(studentService.getAttendanceRecords(studentId));
    }

    @ApiOperation("保存学生考勤记录")
    @PostMapping("/{studentId}/attendance")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public CommonResult<Void> saveAttendanceRecords(
            @PathVariable Long studentId,
            @Validated @RequestBody List<AttendanceRecord> records) {
        studentService.saveAttendanceRecords(studentId, records);
        return CommonResult.success(null);
    }
}
