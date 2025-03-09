package com.good.physicalexercisesystem.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TeacherTodoVO {
    private Long id;
    private String title;
    private String content;
    private String type;
    private String status;
    private LocalDateTime dueDate;
} 