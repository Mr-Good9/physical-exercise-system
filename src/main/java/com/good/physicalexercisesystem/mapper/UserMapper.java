package com.good.physicalexercisesystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.good.physicalexercisesystem.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
} 