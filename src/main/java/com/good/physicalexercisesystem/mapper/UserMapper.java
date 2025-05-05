package com.good.physicalexercisesystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.good.physicalexercisesystem.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    Page<User> selectUserPage(
            Page<User> page,
            @Param("username") String username,
            @Param("userType") String userType
    );

    /**
     * 根据关键词搜索用户
     */
    @Select("SELECT * FROM sys_user WHERE deleted = 0 AND user_type = 'student' AND (name LIKE CONCAT('%', #{keyword}, '%') OR username LIKE CONCAT('%', #{keyword}, '%'))")
    List<User> selectUsersByKeyword(@Param("keyword") String keyword);
}
