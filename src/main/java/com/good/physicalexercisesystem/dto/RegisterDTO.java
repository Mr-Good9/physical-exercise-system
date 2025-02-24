package com.good.physicalexercisesystem.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class RegisterDTO {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 20, message = "用户名长度必须在4-20个字符之间")
    private String username;

    @NotBlank(message = "密码不能为空") 
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String password;

    @NotBlank(message = "用户类型不能为空")
    @Pattern(regexp = "^(student|teacher)$", message = "用户类型必须是student或teacher")
    private String userType;

    @NotBlank(message = "真实姓名不能为空")
    private String name;
} 