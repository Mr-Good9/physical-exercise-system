package com.good.physicalexercisesystem.dto;

import lombok.Data;

@Data
public class PhysicalTestQuery {
    private String name;
    private String className;
    private Long testItem;
}