package com.good.physicalexercisesystem.controller;

import com.good.physicalexercisesystem.common.ApiResponse;
import com.good.physicalexercisesystem.dto.LoginDTO;
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
    public ApiResponse<Map<String, Object>> login(@Validated @RequestBody LoginDTO loginDTO) {
        String token = userService.login(loginDTO.getUsername(), loginDTO.getPassword(), loginDTO.getUserType());
        User user = userService.findByUsername(loginDTO.getUsername());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo", user);

        return ApiResponse.success("登录成功", result);
    }

    @PostMapping("/register")
    public ApiResponse<Void> register(@Validated @RequestBody User user) {
        userService.register(user);
        return ApiResponse.success("注册成功", null);
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@RequestParam String username,
                                         @RequestParam String oldPassword,
                                         @RequestParam String newPassword) {
        userService.resetPassword(username, oldPassword, newPassword);
        return ApiResponse.success("密码重置成功", null);
    }
} 