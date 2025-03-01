package com.good.physicalexercisesystem.controller;

import com.good.physicalexercisesystem.common.CommonResult;
import com.good.physicalexercisesystem.dto.UpdatePasswordDTO;
import com.good.physicalexercisesystem.dto.UpdateProfileDTO;
import com.good.physicalexercisesystem.entity.User;
import com.good.physicalexercisesystem.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 获取当前用户的个人信息
     * @param authentication 当前登录用户的认证信息
     * @return 用户信息(不包含密码)
     */
    @GetMapping("/profile")
    public CommonResult<User> getProfile(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        user.setPassword(null); // 不返回密码
        return CommonResult.success(user);
    }

    /**
     * 更新用户个人信息
     * @param authentication 当前登录用户的认证信息
     * @param profileDTO 要更新的个人信息
     * @return 更新结果
     */
    @PutMapping("/profile")
    public CommonResult<Void> updateProfile(
            Authentication authentication,
            @Validated @RequestBody UpdateProfileDTO profileDTO
    ) {
        User user = userService.findByUsername(authentication.getName());
        userService.updateProfile(user.getId(), profileDTO);
        return CommonResult.success("更新成功", null);
    }

    /**
     * 修改用户密码
     * @param passwordDTO 密码信息(包含旧密码和新密码)
     * @return 修改结果
     */
    @PutMapping("/password")
    public CommonResult<Void> updatePassword(
            @Validated @RequestBody UpdatePasswordDTO passwordDTO
    ) {
        if (!passwordDTO.getNewPassword().equals(passwordDTO.getConfirmPassword())) {
            return CommonResult.error("两次输入的密码不一致");
        }
        userService.updatePassword(passwordDTO);
        return CommonResult.success("密码修改成功", null);
    }

    /**
     * 上传用户头像
     * @param authentication 当前登录用户的认证信息
     * @param file 头像文件(JPG/PNG格式，大小不超过2MB)
     * @return 头像访问URL
     */
    @PostMapping("/avatar")
    public CommonResult<String> uploadAvatar(
            Authentication authentication,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            // 文件类型校验
            String contentType = file.getContentType();
            if (contentType == null || (!contentType.startsWith("image/jpeg") && !contentType.startsWith("image/png"))) {
                return CommonResult.error("只支持JPG/PNG格式的图片");
            }

            // 文件大小校验(2MB)
            if (file.getSize() > 2 * 1024 * 1024) {
                return CommonResult.error("文件大小不能超过2MB");
            }

            User user = userService.findByUsername(authentication.getName());
            String avatarUrl = userService.uploadAvatar(user.getId(), file);
            return CommonResult.success(avatarUrl);
        } catch (Exception e) {
            return CommonResult.error(e.getMessage());
        }
    }
}
