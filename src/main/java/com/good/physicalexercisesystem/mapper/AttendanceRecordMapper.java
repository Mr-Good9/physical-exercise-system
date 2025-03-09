package com.good.physicalexercisesystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.good.physicalexercisesystem.entity.AttendanceRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AttendanceRecordMapper extends BaseMapper<AttendanceRecord> {
    
    /**
     * 批量插入考勤记录
     */
    void insertBatch(@Param("records") List<AttendanceRecord> records);
    
    /**
     * 计算学生的出勤率
     */
    Double calculateAttendanceRate(@Param("studentId") Long studentId);
} 