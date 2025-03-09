package com.good.physicalexercisesystem.dto;

import lombok.Data;

@Data
public class StudentInfo {
    private Long id;
    private Long userId;
    private String username;
    private String name;
    private String userType;
    private String gender;
    private String phone;
    private String email;
    private String avatar;
    private String studentId;
    private String className;
    private Integer attendance;
    private Double physicalScore;
}
