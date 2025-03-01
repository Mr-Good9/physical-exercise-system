package com.good.physicalexercisesystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * id: 主键
 * title: 标题
 * content: 内容
 * type: 通知类型
 * enabled: 是否启用
 * create_time: 创建时间
 * update_time: 更新时间
 * deleted: 逻辑删除标记
 */
@Data
@TableName("pe_notice")
public class Notice {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String content;
    private String type;
    private Integer enabled;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
