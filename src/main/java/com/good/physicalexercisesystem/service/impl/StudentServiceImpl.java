package com.good.physicalexercisesystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.good.physicalexercisesystem.common.exception.CustomException;
import com.good.physicalexercisesystem.entity.*;
import com.good.physicalexercisesystem.dto.StudentDTO;
import com.good.physicalexercisesystem.dto.PhysicalGradeDTO;
import com.good.physicalexercisesystem.dto.PhysicalTestRecordDTO;
import com.good.physicalexercisesystem.mapper.*;
import com.good.physicalexercisesystem.service.StudentService;
import com.good.physicalexercisesystem.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;
import com.alibaba.excel.EasyExcel;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final UserMapper userMapper;
    private final StudentInfoMapper studentInfoMapper;
    private final PhysicalTestRecordMapper physicalTestRecordMapper;
    private final PhysicalTestItemMapper physicalTestItemMapper;
    private final AttendanceRecordMapper attendanceRecordMapper;
    private final PasswordEncoder passwordEncoder;
    private final PeClassMapper peClassMapper;

    @Override
    public Page<StudentDTO> getStudentList(Integer page, Integer pageSize, String name, String className) {
        Page<User> studentPage = new Page<>(page, pageSize);

        // 如果指定了班级名称，先查询该班级的学生ID
        List<Long> studentIds;
        if (StringUtils.hasText(className)) {
            studentIds = studentInfoMapper.selectList(
                            new LambdaQueryWrapper<StudentInfo>()
                                    .eq(StudentInfo::getClassName, className)
                                    .select(StudentInfo::getUserId)
                    ).stream()
                    .map(StudentInfo::getUserId)
                    .collect(Collectors.toList());

            // 如果没有找到任何学生，直接返回空结果
            if (studentIds.isEmpty()) {
                Page<StudentDTO> emptyPage = new Page<>(page, pageSize, 0);
                emptyPage.setRecords(new ArrayList<>());
                return emptyPage;
            }
        } else {
            studentIds = null;
        }
        // 教的所有班级
        List<PeClass> peClasses = peClassMapper.selectList(
                new QueryWrapper<PeClass>()
                        .select("class_name", "teacher_id")
                        .eq("teacher_id", UserContext.getUser().getId())
        );
        log.info("peClasses: {}", peClasses);
        // 获取教师的所有班级名称
        List<String> classNames = peClasses.stream()
                .map(PeClass::getClassName)
                .collect(Collectors.toList());
        // 查询学生基本信息
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .eq(User::getUserType, "student")
                .like(StringUtils.hasText(name), User::getName, name)
                .in(studentIds != null, User::getId, studentIds)
            .orderByDesc(User::getCreateTime);

        Page<User> studentResult = userMapper.selectPage(studentPage, wrapper);

        // 转换为DTO并填充扩展信息
        Page<StudentDTO> dtoPage = new Page<>(studentResult.getCurrent(), studentResult.getSize(), studentResult.getTotal());
        dtoPage.setRecords(studentResult.getRecords().stream().map(student -> {
            StudentDTO dto = convertToDTO(student);
            // 查询学生扩展信息
            StudentInfo studentInfo = studentInfoMapper.selectOne(
                    new LambdaQueryWrapper<StudentInfo>()
                            .eq(StudentInfo::getUserId, student.getId())
            );
            if (studentInfo != null) {
                dto.setStudentId(studentInfo.getStudentId());
                dto.setClassName(studentInfo.getClassName());
                dto.setGrade(studentInfo.getGrade());
                dto.setMajor(studentInfo.getMajor());
            }
            return dto;
        }).collect(Collectors.toList()));
        // 过滤掉不属于教师所教班级的学生
        dtoPage.getRecords().removeIf(dto -> !classNames.contains(dto.getClassName()));
        return dtoPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addStudent(StudentDTO studentDTO) {
        // 检查学号是否已存在
        if (checkStudentIdExists(studentDTO.getStudentId())) {
            throw new CustomException("学号已存在");
        }

        // 保存学生基本信息
        User student = new User();
        student.setUsername(studentDTO.getStudentId()); // 使用学号作为用户名
        student.setPassword(passwordEncoder.encode("123456")); // 默认密码
        student.setName(studentDTO.getName());
        student.setUserType("student");
        student.setPhone(studentDTO.getPhone());
        student.setEmail(studentDTO.getEmail());
        student.setGender(studentDTO.getGender());
        student.setAvatar(studentDTO.getAvatar());
        student.setEnabled(true);
        userMapper.insert(student);

        // 保存学生扩展信息
        StudentInfo studentInfo = new StudentInfo();
        studentInfo.setUserId(student.getId());
        studentInfo.setStudentId(studentDTO.getStudentId());
        studentInfo.setClassName(studentDTO.getClassName());
        studentInfo.setGrade(studentDTO.getGrade());
        studentInfo.setMajor(studentDTO.getMajor());
        studentInfoMapper.insert(studentInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStudent(Long id, StudentDTO studentDTO) {
        // 检查学生是否存在
        User student = userMapper.selectById(id);
        if (student == null) {
            throw new CustomException("学生不存在");
        }

        // 如果修改了学号，需要检查是否与其他学生重复
        if (!student.getUsername().equals(studentDTO.getStudentId())) {
            if (checkStudentIdExists(studentDTO.getStudentId())) {
                throw new CustomException("学号已存在");
            }
            student.setUsername(studentDTO.getStudentId());
        }

        // 更新学生基本信息
        student.setName(studentDTO.getName());
        student.setPhone(studentDTO.getPhone());
        student.setEmail(studentDTO.getEmail());
        student.setGender(studentDTO.getGender());
        student.setAvatar(studentDTO.getAvatar());
        userMapper.updateById(student);

        // 更新学生扩展信息
        StudentInfo studentInfo = studentInfoMapper.selectOne(
                new LambdaQueryWrapper<StudentInfo>()
                        .eq(StudentInfo::getUserId, id)
        );
        if (studentInfo != null) {
            studentInfo.setStudentId(studentDTO.getStudentId());
            studentInfo.setClassName(studentDTO.getClassName());
            studentInfo.setGrade(studentDTO.getGrade());
            studentInfo.setMajor(studentDTO.getMajor());
            studentInfoMapper.updateById(studentInfo);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteStudent(Long id) {
        // 删除学生基本信息（逻辑删除）
        userMapper.deleteById(id);

        // 删除学生扩展信息（逻辑删除）
        studentInfoMapper.delete(
                new LambdaQueryWrapper<StudentInfo>()
                        .eq(StudentInfo::getUserId, id)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importStudents(MultipartFile file) {
        try {
            List<StudentDTO> list = EasyExcel.read(file.getInputStream())
                    .head(StudentDTO.class)
                    .sheet()
                    .doReadSync();

            for (StudentDTO dto : list) {
                addStudent(dto);
            }
        } catch (Exception e) {
            throw new CustomException("导入失败：" + e.getMessage());
        }
    }

    @Override
    public List<PhysicalGradeDTO> getPhysicalGrades(Long studentId) {
        List<PhysicalTestRecord> records = physicalTestRecordMapper.selectList(
                new LambdaQueryWrapper<PhysicalTestRecord>()
                        .eq(PhysicalTestRecord::getStudentId, studentId)
                        .orderByDesc(PhysicalTestRecord::getTestDate)
        );
        if (records.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> itemIds = records.stream().map(PhysicalTestRecord::getTestItemId).distinct().collect(Collectors.toList());
        // k-v 项目id，项目信息
        Map<Long, PhysicalTestItem> itemMap =
                physicalTestItemMapper.selectList(
                        new LambdaQueryWrapper<PhysicalTestItem>()
                                .in(PhysicalTestItem::getId, itemIds)
                ).stream().collect(Collectors.toMap(PhysicalTestItem::getId, u -> u));
        return records.stream().map(record -> {
            PhysicalGradeDTO dto = new PhysicalGradeDTO();
            BeanUtils.copyProperties(record, dto);
            dto.setItem(itemMap.get(record.getTestItemId()).getItemName());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePhysicalGrades(Long studentId, List<PhysicalGradeDTO> grades) {
        // 删除原有记录
        physicalTestRecordMapper.delete(
                new LambdaQueryWrapper<PhysicalTestRecord>()
                        .eq(PhysicalTestRecord::getStudentId, studentId)
        );

        // 获取所有测试项目
        Map<String, Long> itemMap = physicalTestItemMapper.selectList(null)
                .stream()
                .collect(Collectors.toMap(PhysicalTestItem::getItemName, PhysicalTestItem::getId));

        // 保存新记录
        List<PhysicalTestRecord> records = grades.stream().map(grade -> {
            PhysicalTestRecord record = new PhysicalTestRecord();
            record.setStudentId(studentId);
            record.setTestItemId(itemMap.get(grade.getItem())); // 根据项目名称获取项目ID
            record.setScore(grade.getScore());
            record.setEvaluation(grade.getEvaluation());
            record.setTestDate(grade.getTestDate());
            return record;
        }).collect(Collectors.toList());

        // 批量插入记录
        records.forEach(record -> {
            if (record.getTestItemId() == null) {
                throw new CustomException("测试项目不存在：" + record.getTestResult());
            }
            physicalTestRecordMapper.insert(record);
        });
    }

    @Override
    public List<AttendanceRecord> getAttendanceRecords(Long studentId) {
        return attendanceRecordMapper.selectList(
                new LambdaQueryWrapper<AttendanceRecord>()
                        .eq(AttendanceRecord::getStudentId, studentId)
                        .orderByDesc(AttendanceRecord::getAttendanceDate)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAttendanceRecords(Long studentId, List<AttendanceRecord> records) {
        records.forEach(record -> {
            record.setStudentId(studentId);
            attendanceRecordMapper.insert(record);
        });
    }

    @Override
    public Long countStudentsByTeacher(Long teacherId) {
        // 获取教师所教的班级
        List<PeClass> classes = peClassMapper.selectList(
            new LambdaQueryWrapper<PeClass>()
                .eq(PeClass::getTeacherId, teacherId)
        );

        if (classes.isEmpty()) {
            return 0L;
        }

        // 获取这些班级的学生数量
        List<String> classNames = classes.stream()
            .map(PeClass::getClassName)
            .collect(Collectors.toList());

        return studentInfoMapper.selectCount(
            new LambdaQueryWrapper<StudentInfo>()
                .in(StudentInfo::getClassName, classNames)
        );
    }

    @Override
    public Double getAverageAttendanceRate(Long teacherId) {
        return null;
    }

//    @Override
//    public Double getAverageAttendanceRate(Long teacherId) {
//        // 获取教师所教班级的学生ID列表
//        List<Long> studentIds = getTeacherStudentIds(teacherId);
//        if (studentIds.isEmpty()) {
//            return 0.0;
//        }
//
//        // 计算这些学生的平均出勤率
//        return attendanceRecordMapper.calculateAverageAttendanceRate(studentIds);
//    }

    @Override
    public Double getAveragePhysicalScore(Long teacherId) {
        // 获取教师所教班级的学生ID列表
        List<Long> studentIds = getTeacherStudentIds(teacherId);
        if (studentIds.isEmpty()) {
            return 0.0;
        }

        // 计算这些学生的平均体测成绩
        List<PhysicalTestRecord> records = physicalTestRecordMapper.selectList(
            new LambdaQueryWrapper<PhysicalTestRecord>()
                .in(PhysicalTestRecord::getStudentId, studentIds)
        );

        if (records.isEmpty()) {
            return 0.0;
        }

        return records.stream()
            .mapToInt(PhysicalTestRecord::getScore)
            .average()
            .orElse(0.0);
    }

    @Override
    public List<PhysicalTestRecordDTO> getRecentPhysicalTests(Long teacherId, Integer limit) {
        // 获取教师所教班级的学生ID列表
        List<Long> studentIds = getTeacherStudentIds(teacherId);
        if (studentIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取最近的体测记录
        List<PhysicalTestRecord> records = physicalTestRecordMapper.selectList(
            new LambdaQueryWrapper<PhysicalTestRecord>()
                .in(PhysicalTestRecord::getStudentId, studentIds)
                .orderByDesc(PhysicalTestRecord::getTestDate)
                .last("LIMIT " + limit)
        );

        // 转换为DTO
        return records.stream().map(record -> {
            PhysicalTestRecordDTO dto = new PhysicalTestRecordDTO();
            BeanUtils.copyProperties(record, dto);
            // 获取学生信息
            User student = userMapper.selectById(record.getStudentId());
            if (student != null) {
                dto.setStudentName(student.getName());
            }
            // 获取测试项目信息
            PhysicalTestItem item = physicalTestItemMapper.selectById(record.getTestItemId());
            if (item != null) {
                dto.setTestItemName(item.getItemName());
            }
            return dto;
        }).collect(Collectors.toList());
    }

    // 获取教师所教班级的所有学生ID
    private List<Long> getTeacherStudentIds(Long teacherId) {
        // 获取教师所教的班级
        List<PeClass> classes = peClassMapper.selectList(
            new LambdaQueryWrapper<PeClass>()
                .eq(PeClass::getTeacherId, teacherId)
        );

        if (classes.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取这些班级的学生ID
        List<String> classNames = classes.stream()
            .map(PeClass::getClassName)
            .collect(Collectors.toList());

        return studentInfoMapper.selectList(
            new LambdaQueryWrapper<StudentInfo>()
                .in(StudentInfo::getClassName, classNames)
                .select(StudentInfo::getUserId)
        ).stream()
        .map(StudentInfo::getUserId)
        .collect(Collectors.toList());
    }

    private StudentDTO convertToDTO(User student) {
        StudentDTO dto = new StudentDTO();
        // 复制基本信息
        dto.setId(student.getId());
        dto.setName(student.getName());
        dto.setPhone(student.getPhone());
        dto.setEmail(student.getEmail());
        dto.setGender(student.getGender());
        dto.setAvatar(student.getAvatar());
        dto.setEnabled(student.getEnabled());
        dto.setCreateTime(student.getCreateTime());
        dto.setUpdateTime(student.getUpdateTime());

        // 计算考勤率
        Double attendance = attendanceRecordMapper.calculateAttendanceRate(student.getId());
        dto.setAttendance(attendance != null ? attendance : 0.0);

        // 计算体测成绩平均分
        Double physicalScore = calculatePhysicalScore(student.getId());
        dto.setPhysicalScore(physicalScore);

        return dto;
    }

    private boolean checkStudentIdExists(String studentId) {
        return studentInfoMapper.selectCount(
                new LambdaQueryWrapper<StudentInfo>()
                        .eq(StudentInfo::getStudentId, studentId)
        ) > 0;
    }

    private Double calculatePhysicalScore(Long studentId) {
        List<PhysicalTestRecord> records = physicalTestRecordMapper.selectList(
                new LambdaQueryWrapper<PhysicalTestRecord>()
                        .eq(PhysicalTestRecord::getStudentId, studentId)
        );

        if (records.isEmpty()) {
            return 0.0;
        }

        return records.stream()
                .mapToInt(PhysicalTestRecord::getScore)
                .average()
                .orElse(0.0);
    }
}
