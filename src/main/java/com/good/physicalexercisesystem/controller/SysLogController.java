// 控制器
package com.good.physicalexercisesystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.good.physicalexercisesystem.common.CommonResult;
import com.good.physicalexercisesystem.dto.LogDTO;
import com.good.physicalexercisesystem.service.SysLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
public class SysLogController {

    private final SysLogService sysLogService;

    @GetMapping
    public CommonResult<Page<LogDTO>> getLogList(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        Page<LogDTO> logPage = sysLogService.getLogList(type, level, startTime, endTime, pageNum, pageSize);
        return CommonResult.success(logPage);
    }

    @DeleteMapping("/{id}")
    public CommonResult<Boolean> deleteLog(@PathVariable Long id) {
        boolean success = sysLogService.deleteLog(id);
        return success ? CommonResult.success(true) : CommonResult.error("删除日志失败");
    }

    @DeleteMapping("/clear")
    public CommonResult<Boolean> clearLogs() {
        boolean success = sysLogService.clearLogs();
        return success ? CommonResult.success(true) : CommonResult.error("清空日志失败");
    }

    @GetMapping("/export")
    public void exportLogs(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            HttpServletResponse response) throws IOException {

        List<LogDTO> logs = sysLogService.exportLogs(type, level, startTime, endTime);

        // 设置响应头
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = "系统日志_" + System.currentTimeMillis() + ".csv";
        response.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));

        // CSV header
        String header = "日志ID,日志类型,日志级别,日志内容,操作人,浏览器,创建时间,请求参数\n";
        response.getOutputStream().write(header.getBytes(StandardCharsets.UTF_8));

        // 写入数据
        for (LogDTO log : logs) {
            StringBuilder sb = new StringBuilder();
            sb.append(log.getId()).append(",")
              .append(log.getType()).append(",")
              .append(log.getLevel()).append(",")
              .append(log.getContent().replace(",", "，")).append(",")
              .append(log.getOperator()).append(",")
              .append(log.getBrowser()).append(",")
              .append(log.getCreateTime()).append(",")
              .append(log.getParams() != null ? log.getParams().replace(",", "，").replace("\n", " ") : "").append("\n");

            response.getOutputStream().write(sb.toString().getBytes(StandardCharsets.UTF_8));
        }
    }
}
