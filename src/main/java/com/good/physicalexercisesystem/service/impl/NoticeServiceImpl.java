package com.good.physicalexercisesystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.good.physicalexercisesystem.entity.Notice;
import com.good.physicalexercisesystem.mapper.NoticeMapper;
import com.good.physicalexercisesystem.service.NoticeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeServiceImpl implements NoticeService {
    
    private final NoticeMapper noticeMapper;
    
    public NoticeServiceImpl(NoticeMapper noticeMapper) {
        this.noticeMapper = noticeMapper;
    }

    @Override
    public List<Notice> getRecentNotices() {
        return noticeMapper.selectList(
            new LambdaQueryWrapper<Notice>()
                .eq(Notice::getEnabled, 1)
                .orderByDesc(Notice::getCreateTime)
                .last("LIMIT 10")
        );
    }
} 