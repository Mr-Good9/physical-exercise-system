package com.good.physicalexercisesystem.service;

import com.good.physicalexercisesystem.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.validation.constraints.NotBlank;

public interface UserService extends IService<User> {
    User findByUsername(String username);
    void register(User user);
    String login(String username, String password, String userType);
    void resetPassword(String username, String oldPassword, String newPassword);
}
