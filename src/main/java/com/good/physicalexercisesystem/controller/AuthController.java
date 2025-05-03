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

    @PostMapping("/reset-password")
    @Log(value = "重置密码", level = "info")
    public CommonResult<Void> resetPassword(@RequestParam String username,
                                            @RequestParam String oldPassword,
                                            @RequestParam String newPassword) {
        userService.resetPassword(username, oldPassword, newPassword);
        return CommonResult.success("密码重置成功", null);
    }
}
