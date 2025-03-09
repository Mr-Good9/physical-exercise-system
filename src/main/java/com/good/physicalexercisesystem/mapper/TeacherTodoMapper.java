package com.good.physicalexercisesystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.good.physicalexercisesystem.entity.TeacherTodo;
import com.good.physicalexercisesystem.vo.TeacherTodoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TeacherTodoMapper extends BaseMapper<TeacherTodo> {
    List<TeacherTodoVO> selectTeacherTodos(@Param("teacherId") Long teacherId);
} 