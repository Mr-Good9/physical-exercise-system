package com.good.physicalexercisesystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.good.physicalexercisesystem.entity.PeClass;
import com.good.physicalexercisesystem.dto.ClassDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 班级Mapper接口
 */
@Mapper
public interface PeClassMapper extends BaseMapper<PeClass> {
    IPage<ClassDTO> selectClassList(IPage<ClassDTO> page, @Param("teacherId") Long teacherId);
    
    Integer countStudentsByClassId(@Param("classId") Long classId);
} 