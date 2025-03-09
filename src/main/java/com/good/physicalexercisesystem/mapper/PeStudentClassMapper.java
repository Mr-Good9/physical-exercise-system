package com.good.physicalexercisesystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.good.physicalexercisesystem.entity.PeStudentClass;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 学生班级关联表 Mapper 接口
 * </p>
 *
 * @author chris
 * @since 2025-03-09
 */
@Mapper
public interface PeStudentClassMapper extends BaseMapper<PeStudentClass> {

}
