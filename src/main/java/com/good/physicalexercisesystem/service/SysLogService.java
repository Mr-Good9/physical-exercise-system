// 服务接口
package com.good.physicalexercisesystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.good.physicalexercisesystem.dto.LogDTO;
import com.good.physicalexercisesystem.entity.SysLog;

import java.time.LocalDateTime;
import java.util.List;

public interface SysLogService extends IService<SysLog> {

    // 分页查询日志
    Page<LogDTO> getLogList(String type, String level, LocalDateTime startTime, LocalDateTime endTime, Integer pageNum, Integer pageSize);

    // 删除日志
    boolean deleteLog(Long id);

    // 清空日志
    boolean clearLogs();

    // 导出日志
    List<LogDTO> exportLogs(String type, String level, LocalDateTime startTime, LocalDateTime endTime);

    // 添加日志
    boolean addLog(String type, String level, String content, String operator, String browser, String params);
}
