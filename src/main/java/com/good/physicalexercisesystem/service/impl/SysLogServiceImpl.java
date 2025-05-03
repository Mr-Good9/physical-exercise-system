// 服务实现类
package com.good.physicalexercisesystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.good.physicalexercisesystem.dto.LogDTO;
import com.good.physicalexercisesystem.entity.SysLog;
import com.good.physicalexercisesystem.mapper.SysLogMapper;
import com.good.physicalexercisesystem.service.SysLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog> implements SysLogService {

    @Override
    public Page<LogDTO> getLogList(String type, String level, LocalDateTime startTime, LocalDateTime endTime, Integer pageNum, Integer pageSize) {
        // 构建查询条件
        LambdaQueryWrapper<SysLog> queryWrapper = new LambdaQueryWrapper<>();

        // 添加查询条件
        if (type != null && !type.isEmpty()) {
            queryWrapper.eq(SysLog::getType, type);
        }

        if (level != null && !level.isEmpty()) {
            queryWrapper.eq(SysLog::getLevel, level);
        }

        if (startTime != null) {
            queryWrapper.ge(SysLog::getCreateTime, startTime);
        }

        if (endTime != null) {
            queryWrapper.le(SysLog::getCreateTime, endTime);
        }

        // 按创建时间降序排序
        queryWrapper.orderByDesc(SysLog::getCreateTime);

        // 分页查询
        Page<SysLog> page = new Page<>(pageNum, pageSize);
        Page<SysLog> logPage = this.page(page, queryWrapper);

        // 转换为DTO
        Page<LogDTO> dtoPage = new Page<>(logPage.getCurrent(), logPage.getSize(), logPage.getTotal());
        List<LogDTO> dtoList = logPage.getRecords().stream().map(log -> {
            LogDTO dto = new LogDTO();
            BeanUtils.copyProperties(log, dto);
            return dto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(dtoList);
        return dtoPage;
    }

    @Override
    @Transactional
    public boolean deleteLog(Long id) {
        return this.removeById(id);
    }

    @Override
    @Transactional
    public boolean clearLogs() {
        return this.getBaseMapper().delete(null) > 0;
    }

    @Override
    public List<LogDTO> exportLogs(String type, String level, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<SysLog> queryWrapper = new LambdaQueryWrapper<>();

        if (type != null && !type.isEmpty()) {
            queryWrapper.eq(SysLog::getType, type);
        }

        if (level != null && !level.isEmpty()) {
            queryWrapper.eq(SysLog::getLevel, level);
        }

        if (startTime != null) {
            queryWrapper.ge(SysLog::getCreateTime, startTime);
        }

        if (endTime != null) {
            queryWrapper.le(SysLog::getCreateTime, endTime);
        }

        queryWrapper.orderByDesc(SysLog::getCreateTime);

        List<SysLog> logs = this.list(queryWrapper);

        return logs.stream().map(log -> {
            LogDTO dto = new LogDTO();
            BeanUtils.copyProperties(log, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean addLog(String type, String level, String content, String operator, String browser, String params) {
        SysLog log = new SysLog();
        log.setType(type);
        log.setLevel(level);
        log.setContent(content);
        log.setOperator(operator);
        log.setBrowser(browser);
        log.setParams(params);

        return this.save(log);
    }
}
