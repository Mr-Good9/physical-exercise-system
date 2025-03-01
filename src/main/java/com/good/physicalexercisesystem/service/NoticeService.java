package com.good.physicalexercisesystem.service;

import com.good.physicalexercisesystem.entity.Notice;
import java.util.List;

public interface NoticeService {
    List<Notice> getRecentNotices();
} 