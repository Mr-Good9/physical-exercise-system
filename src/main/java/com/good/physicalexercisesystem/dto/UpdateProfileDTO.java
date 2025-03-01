package com.good.physicalexercisesystem.dto;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Data
public class UpdateProfileDTO {
    private String name;
    
    @Pattern(regexp = "^(male|female)$", message = "性别必须是male或female")
    private String gender;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    @Email(message = "邮箱格式不正确")
    private String email;

    private String avatar;
} 