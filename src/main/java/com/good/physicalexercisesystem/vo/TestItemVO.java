package com.good.physicalexercisesystem.vo;

import lombok.Data;

@Data
public class TestItemVO {
    private Long id;
    private String name;
    private String code;
    private String type;
    private String unit;
    private String standardValue;
    private String scoringRules;
    private String description;
    private Boolean enabled;
} 