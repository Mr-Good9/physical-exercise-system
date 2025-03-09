package com.good.physicalexercisesystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.good.physicalexercisesystem.entity.Course;
import com.good.physicalexercisesystem.vo.CourseQuery;
import com.good.physicalexercisesystem.vo.CourseVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CourseMapper extends BaseMapper<Course> {
    /**
     * 分页查询课程列表
     */
    IPage<CourseVO> selectCourseList(Page<CourseVO> page, @Param("query") CourseQuery query);
} 