package com.good.physicalexercisesystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.good.physicalexercisesystem.dto.PhysicalTestRecordDTO;
import com.good.physicalexercisesystem.dto.StudentDTO;
import com.good.physicalexercisesystem.dto.PhysicalGradeDTO;
import com.good.physicalexercisesystem.dto.StudentInfo;
import com.good.physicalexercisesystem.entity.AttendanceRecord;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface StudentService {

    /**
     * 分页查询学生列表
     */
    Page<StudentDTO> getStudentList(Integer page, Integer pageSize, String name, String className);

    /**
     * 添加学生
     */
    void addStudent(StudentDTO studentDTO);

    /**
     * 更新学生信息
     */
    void updateStudent(Long id, StudentDTO studentDTO);

    /**
     * 删除学生
     */
    void deleteStudent(Long id);

    /**
     * 批量导入学生
     */
    void importStudents(MultipartFile file);

    /**
     * 获取学生体测成绩
     */
    List<PhysicalGradeDTO> getPhysicalGrades(Long studentId);

    /**
     * 保存学生体测成绩
     */
    void savePhysicalGrades(Long studentId, List<PhysicalGradeDTO> grades);

    /**
     * 获取学生考勤记录
     */
    List<AttendanceRecord> getAttendanceRecords(Long studentId);

    /**
     * 保存学生考勤记录
     */
    void saveAttendanceRecords(Long studentId, List<AttendanceRecord> records);

    /**
     * 统计教师的学生总数
     */
    Long countStudentsByTeacher(Long teacherId);

    /**
     * 获取平均出勤率
     */
    Double getAverageAttendanceRate(Long teacherId);

    /**
     * 获取平均体测成绩
     */
    Double getAveragePhysicalScore(Long teacherId);

    /**
     * 获取最近的体测记录
     */
    List<PhysicalTestRecordDTO> getRecentPhysicalTests(Long teacherId, Integer limit);
}
