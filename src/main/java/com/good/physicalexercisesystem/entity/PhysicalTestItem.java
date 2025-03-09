package com.good.physicalexercisesystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 *   `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
 *   `item_name` varchar(50) NOT NULL COMMENT '项目名称',
 *   `item_code` varchar(50) NOT NULL COMMENT '项目代码',
 *   `description` text COMMENT '项目描述',
 *   `unit` varchar(20) DEFAULT NULL COMMENT '计量单位',
 *   `enabled` tinyint(1) DEFAULT '1' COMMENT '是否启用:1-启用,0-禁用',
 *   `create_time` datetime NOT NULL COMMENT '创建时间',
 *   `update_time` datetime NOT NULL COMMENT '更新时间',
 *   `deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除:1-已删除,0-未删除',
 *   `standard_value` json DEFAULT NULL COMMENT '标准值配置',
 *   `scoring_rules` json DEFAULT NULL COMMENT '评分规则',
 *   PRIMARY KEY (`id`),
 */
@Data
@TableName("physical_test_item")
public class PhysicalTestItem {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 项目名称
     */
    private String itemName;
    /**
     * 项目代码
     */
    private String itemCode;
    /**
     * 项目描述
     */
    private String description;
    /**
     * 计量单位
     */
    private String unit;
    /**
     * 是否启用:1-启用,0-禁用
     */
    private Integer enabled;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    /**
     * 是否删除:1-已删除,0-未删除
     */
    @TableLogic
    private Integer deleted;
}
