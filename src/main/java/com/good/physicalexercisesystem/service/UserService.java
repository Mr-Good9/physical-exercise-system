package com.good.physicalexercisesystem.service;

import com.good.physicalexercisesystem.dto.UpdatePasswordDTO;
import com.good.physicalexercisesystem.dto.UpdateProfileDTO;
import com.good.physicalexercisesystem.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

public interface UserService extends IService<User> {
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户信息
     */
    User findByUsername(String username);

    /**
     * 用户注册
     * @param user 用户信息
     */
    void register(User user);

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @param userType 用户类型
     * @return JWT token
     */
    String login(String username, String password, String userType);

    /**
     * 重置用户密码
     * @param username 用户名
     * @param oldPassword 原密码
     * @param newPassword 新密码
     */
    void resetPassword(String username, String oldPassword, String newPassword);

    /**
     * 更新用户个人信息
     * @param userId 用户ID
     * @param profileDTO 要更新的信息
     */
    void updateProfile(Long userId, UpdateProfileDTO profileDTO);

    /**
     * 上传用户头像
     * @param userId 用户ID
     * @param file 头像文件
     * @return 头像访问URL
     */
    String uploadAvatar(Long userId, MultipartFile file);

    /**
     * 更新用户密码
     * @param passwordDTO
     */
    void updatePassword(UpdatePasswordDTO passwordDTO);

}
