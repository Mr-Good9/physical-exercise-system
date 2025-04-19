package com.good.physicalexercisesystem.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Data
public class UserDTO {

    private Long id;

    @NotBlank(message = "用户名不能为空")
    private String username;

    private String password;

    @NotBlank(message = "真实姓名不能为空")
    private String name;

    @NotBlank(message = "用户类型不能为空")
    private String userType;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入有效的手机号码")
    private String phone;

    @Email(message = "请输入有效的邮箱地址")
    private String email;

    private String gender;

    private String avatar;

    private Boolean enabled;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    // 扩展字段，根据用户类型不同而变化
    private String studentId;
    private String className;
    private String grade;
    private String major;

    private String teacherCode;
    private String title;
}
