package com.good.physicalexercisesystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.good.physicalexercisesystem.entity.CourseAttendance;
import com.good.physicalexercisesystem.vo.CourseAttendanceVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CourseAttendanceMapper extends BaseMapper<CourseAttendance> {
    /**
     * 查询课程考勤列表
     */
    List<CourseAttendanceVO> selectAttendanceList(@Param("courseId") Long courseId);
}
