package com.good.physicalexercisesystem.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author chris
 * @since 2025/3/9 15:02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileVo {

    /**
     * 用户名
     */
    private String username;
    /**
     * 姓名
     */
    private String name;
    /**
     * 用户类型
     */
    private String userType; // admin, teacher, student
    /**
     * 头像
     */
    private String avatar;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 性别
     */
    private String gender;
    /**
     * 是否启用
     */
    private Boolean enabled;
    /**
     * 学生ID
     */
    private Integer studentId;
    /**
     * 班级名称
     */
    private String className;
    /**
     * 教师编号
     */
    private String teacherCode;

}
