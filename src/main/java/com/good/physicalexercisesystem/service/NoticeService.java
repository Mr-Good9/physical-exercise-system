package com.good.physicalexercisesystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.good.physicalexercisesystem.dto.NoticeDTO;
import com.good.physicalexercisesystem.entity.Notice;
import java.util.List;

public interface NoticeService {
    List<Notice> getRecentNotices();

    Page<NoticeDTO> getNoticePage(int current, int size, String title, String type);

    boolean addNotice(NoticeDTO noticeDTO);

    boolean updateNotice(Long id, NoticeDTO noticeDTO);

    boolean toggleNoticeStatus(Long id);

    boolean deleteNotice(Long id);
}
