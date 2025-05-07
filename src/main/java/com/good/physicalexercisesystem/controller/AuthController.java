package com.good.physicalexercisesystem.controller;

import com.good.physicalexercisesystem.annotation.Log;
import com.good.physicalexercisesystem.common.CommonResult;
import com.good.physicalexercisesystem.dto.LoginDTO;
import com.good.physicalexercisesystem.dto.RegisterDTO;
import com.good.physicalexercisesystem.entity.User;
import com.good.physicalexercisesystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录接口
     * 1. 校验用户名、密码、类型是否正确
     * 2. 生成token返回给前端，前端就可以根据token来识别是否是登录状态
     */
    @PostMapping("/login")
    @Log(value = "用户登录", level = "info")
    public CommonResult<Map<String, Object>> login(@Validated @RequestBody LoginDTO loginDTO) {
        String token = userService.login(loginDTO.getUsername(), loginDTO.getPassword(), loginDTO.getUserType());
        User user = userService.findByUsername(loginDTO.getUsername());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo", user);

        return CommonResult.success("登录成功", result);
    }

    /**
     * 用户注册接口
     * 1. 在数据库中查询，判断是否存在这个用户名
     * 2. 对密码加密（使用security的加密器）
     * 3. 将用户信息存入数据库
     * 4. 返回注册成功信息
     */
    @PostMapping("/register")
    @Log(value = "用户注册", level = "info")
    public CommonResult<Void> register(@Validated @RequestBody RegisterDTO registerDTO) {
        // 检查用户名是否已存在
        if (userService.findByUsername(registerDTO.getUsername()) != null) {
            return CommonResult.error("用户名已存在");
        }
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(registerDTO.getPassword());
        user.setUserType(registerDTO.getUserType());
        user.setName(registerDTO.getName());
        user.setEnabled(true);

        userService.register(user);
        return CommonResult.success("注册成功", null);
    }

    /**
     * 未使用
     */
    @PostMapping("/reset-password")
    @Log(value = "重置密码", level = "info")
    public CommonResult<Void> resetPassword(@RequestParam String username,
                                            @RequestParam String oldPassword,
                                            @RequestParam String newPassword) {
        userService.resetPassword(username, oldPassword, newPassword);
        return CommonResult.success("密码重置成功", null);
    }
}
