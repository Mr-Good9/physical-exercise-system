package com.good.physicalexercisesystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.good.physicalexercisesystem.entity.CourseScore;
import com.good.physicalexercisesystem.vo.CourseScoreVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CourseScoreMapper extends BaseMapper<CourseScore> {
    /**
     * 查询课程成绩列表
     */
    List<CourseScoreVO> selectScoreList(@Param("courseId") Long courseId);
}
