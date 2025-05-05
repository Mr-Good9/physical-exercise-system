package com.good.physicalexercisesystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.good.physicalexercisesystem.annotation.Log;
import com.good.physicalexercisesystem.common.CommonResult;
import com.good.physicalexercisesystem.dto.StudentDTO;
import com.good.physicalexercisesystem.dto.UpdatePasswordDTO;
import com.good.physicalexercisesystem.dto.UpdateProfileDTO;
import com.good.physicalexercisesystem.dto.UserDTO;
import com.good.physicalexercisesystem.entity.User;
import com.good.physicalexercisesystem.service.UserService;
import com.good.physicalexercisesystem.utils.UserContext;
import com.good.physicalexercisesystem.vo.UserProfileVo;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 获取当前用户的个人信息
     * @return 用户信息(不包含密码)
     */
    @GetMapping("/profile")
    public CommonResult<UserProfileVo> getProfile() {
         UserProfileVo profile = userService.getProfile(UserContext.getUser().getId());
        return CommonResult.success(profile);
    }

    /**
     * 更新用户个人信息
     * @param authentication 当前登录用户的认证信息
     * @param profileDTO 要更新的个人信息
     * @return 更新结果
     */
    @PutMapping("/profile")
    @Log("更新用户个人信息")
    public CommonResult<Void> updateProfile(
            Authentication authentication,
            @Validated @RequestBody UpdateProfileDTO profileDTO
    ) {
        userService.updateProfile(UserContext.getUser().getId(), profileDTO);
        return CommonResult.success("更新成功", null);
    }

    /**
     * 修改用户密码
     * @param passwordDTO 密码信息(包含旧密码和新密码)
     * @return 修改结果
     */
    @PutMapping("/password")
    @Log(value = "修改用户密码",level = "warning")
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
    @Log("上传用户头像")
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

    @GetMapping("/list")
    public CommonResult<Page<UserDTO>> getUserPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String userType) {

        Page<UserDTO> result = userService.getUserPage(page, pageSize, username, userType);
        return CommonResult.success(result);
    }

    @GetMapping("/{id}")
    public CommonResult<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO userDTO = userService.getUserById(id);
        return userDTO != null ? CommonResult.success(userDTO) : CommonResult.error("用户不存在");
    }

    @PostMapping("/add")
    @Log("添加用户")
    public CommonResult<Boolean> addUser(@RequestBody @Validated UserDTO userDTO) {
        try {
            boolean success = userService.addUser(userDTO);
            return success ? CommonResult.success(true) : CommonResult.error("添加用户失败");
        } catch (Exception e) {
            return CommonResult.error(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    @Log("更新用户")
    public CommonResult<Boolean> updateUser(@PathVariable Long id, @RequestBody @Validated UserDTO userDTO) {
        try {
            boolean success = userService.updateUser(id, userDTO);
            return success ? CommonResult.success(true) : CommonResult.error("更新用户失败");
        } catch (Exception e) {
            return CommonResult.error(e.getMessage());
        }
    }

    @PutMapping("/toggle/{id}")
    @Log("切换用户状态")
    public CommonResult<Boolean> toggleUserStatus(@PathVariable Long id) {
        boolean success = userService.toggleUserStatus(id);
        return success ? CommonResult.success(true) : CommonResult.error("切换用户状态失败");
    }

    @DeleteMapping("/delete/{id}")
    @Log(value = "删除用户",level = "warning")
    public CommonResult<Boolean> deleteUser(@PathVariable Long id) {
        boolean success = userService.deleteUser(id);
        return success ? CommonResult.success(true) : CommonResult.error("删除用户失败");
    }

    @PutMapping("/reset-password/{id}")
    @Log("重置用户密码")
    public CommonResult<Boolean> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> params) {
        String newPassword = params.get("newPassword");
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return CommonResult.error("新密码不能为空");
        }

        boolean success = userService.resetPassword(id, newPassword);
        return success ? CommonResult.success(true) : CommonResult.error("重置密码失败");
    }

    /**
     * 根据关键词搜索学生
     * @param keyword 搜索关键词(姓名或学号)
     * @return 学生列表
     */
    @GetMapping("/search")
    public CommonResult<List<StudentDTO>> searchStudents(@RequestParam String keyword) {
        List<StudentDTO> students = userService.searchStudentsByKeyword(keyword);
        return CommonResult.success(students);
    }
}
