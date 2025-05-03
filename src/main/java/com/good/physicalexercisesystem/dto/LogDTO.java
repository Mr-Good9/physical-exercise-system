// DTO类
package com.good.physicalexercisesystem.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LogDTO {
    private Long id;
    private String type;
    private String level;
    private String content;
    private String operator;
    private String browser;
    private String params;
    private LocalDateTime createTime;
}
