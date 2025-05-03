package com.good.physicalexercisesystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.good.physicalexercisesystem.dto.DashboardStatisticsDTO;
import com.good.physicalexercisesystem.entity.Notice;
import com.good.physicalexercisesystem.entity.SysLog;
import com.good.physicalexercisesystem.entity.User;
import com.good.physicalexercisesystem.mapper.NoticeMapper;
import com.good.physicalexercisesystem.mapper.SysLogMapper;
import com.good.physicalexercisesystem.mapper.UserMapper;
import com.good.physicalexercisesystem.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserMapper userMapper;
    private final NoticeMapper noticeMapper;
    private final SysLogMapper logMapper;

    private final LocalDateTime serverStartTime = LocalDateTime.now();

    @Override
    public DashboardStatisticsDTO getStatistics() {
        DashboardStatisticsDTO statistics = new DashboardStatisticsDTO();

        // 总用户数
        LambdaQueryWrapper<User> userQuery = new LambdaQueryWrapper<>();
        userQuery.eq(User::getDeleted, false);
        int totalUsers = Math.toIntExact(userMapper.selectCount(userQuery));
        statistics.setTotalUsers(totalUsers);

        // 教师人数
        LambdaQueryWrapper<User> teacherQuery = new LambdaQueryWrapper<>();
        teacherQuery.eq(User::getUserType, "teacher").eq(User::getDeleted, false);
        int teacherCount = Math.toIntExact(userMapper.selectCount(teacherQuery));
        statistics.setTeacherCount(teacherCount);

        // 学生人数
        LambdaQueryWrapper<User> studentQuery = new LambdaQueryWrapper<>();
        studentQuery.eq(User::getUserType, "student").eq(User::getDeleted, false);
        int studentCount = Math.toIntExact(userMapper.selectCount(studentQuery));
        statistics.setStudentCount(studentCount);

        // 今日活跃（从日志表计算）
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LambdaQueryWrapper<SysLog> todayLogQuery = new LambdaQueryWrapper<>();
        todayLogQuery.ge(SysLog::getCreateTime, todayStart).eq(SysLog::getDeleted, false);
        int todayLogCount = Math.toIntExact(logMapper.selectCount(todayLogQuery));
        statistics.setActiveToday(todayLogCount);

        // 昨日活跃
        LocalDateTime yesterdayStart = LocalDate.now().minusDays(1).atStartOfDay();
        LocalDateTime yesterdayEnd = LocalDate.now().atStartOfDay();
        LambdaQueryWrapper<SysLog> yesterdayLogQuery = new LambdaQueryWrapper<>();
        yesterdayLogQuery.ge(SysLog::getCreateTime, yesterdayStart)
                         .lt(SysLog::getCreateTime, yesterdayEnd)
                         .eq(SysLog::getDeleted, false);
        int yesterdayLogCount = Math.toIntExact(logMapper.selectCount(yesterdayLogQuery));

        // 计算增长百分比
        double growthRate = 0.0;
        if (yesterdayLogCount > 0) {
            growthRate = (double) (todayLogCount - yesterdayLogCount) / yesterdayLogCount * 100;
        }
        statistics.setActiveGrowth(Math.round(growthRate * 10) / 10.0);

        // 通知数量
        LambdaQueryWrapper<Notice> noticeQuery = new LambdaQueryWrapper<>();
        noticeQuery.eq(Notice::getDeleted, false).eq(Notice::getEnabled, true);
        int noticeCount = Math.toIntExact(noticeMapper.selectCount(noticeQuery));
        statistics.setNoticeCount(noticeCount);

        // 未读通知（假设为最近3条）
        statistics.setUnreadNotice(Math.min(3, noticeCount));

        // 系统运行时间
        Duration uptime = Duration.between(serverStartTime, LocalDateTime.now());
        long days = uptime.toDays();
        long hours = uptime.toHours() % 24;
        statistics.setUptime(days + "天" + hours + "小时");

        return statistics;
    }

    @Override
    public List<Notice> getLatestNotices() {
        LambdaQueryWrapper<Notice> query = new LambdaQueryWrapper<>();
        query.eq(Notice::getDeleted, false)
             .eq(Notice::getEnabled, true)
             .orderByDesc(Notice::getCreateTime)
             .last("LIMIT 4");
        return noticeMapper.selectList(query);
    }

    @Override
    public List<SysLog> getLatestLogs() {
        return logMapper.getLatestLogs();
    }
}
