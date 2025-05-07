package com.good.physicalexercisesystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.good.physicalexercisesystem.dto.NoticeDTO;
import com.good.physicalexercisesystem.entity.Notice;
import com.good.physicalexercisesystem.mapper.NoticeMapper;
import com.good.physicalexercisesystem.service.NoticeService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    private final NoticeMapper noticeMapper;

    public NoticeServiceImpl(NoticeMapper noticeMapper) {
        this.noticeMapper = noticeMapper;
    }

    @Override
    /**
     * 获取最近10条公告
     * 1. 筛选出启动状态的公告
     * 2. 根据创建时间降序排序
     * 3. 取十条
     */
    public List<Notice> getRecentNotices() {
        return noticeMapper.selectList(
                new LambdaQueryWrapper<Notice>()
                        .eq(Notice::getEnabled, 1)
                        .orderByDesc(Notice::getCreateTime)
                        .last("LIMIT 10")
        );
    }

    @Override
    public Page<NoticeDTO> getNoticePage(int current, int size, String title, String type) {
        Page<Notice> page = new Page<>(current, size);
        Page<Notice> noticePage = baseMapper.selectNoticePage(page, title, type);

        Page<NoticeDTO> resultPage = new Page<>(current, size, noticePage.getTotal());
        List<NoticeDTO> records = noticePage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        resultPage.setRecords(records);

        return resultPage;
    }

    @Override
    public boolean addNotice(NoticeDTO noticeDTO) {
        Notice notice = new Notice();
        BeanUtils.copyProperties(noticeDTO, notice);
        notice.setCreateTime(LocalDateTime.now());
        notice.setUpdateTime(LocalDateTime.now());
        notice.setDeleted(0);

        return save(notice);
    }

    @Override
    public boolean updateNotice(Long id, NoticeDTO noticeDTO) {
        Notice notice = getById(id);
        if (notice == null) {
            return false;
        }

        BeanUtils.copyProperties(noticeDTO, notice);
        notice.setUpdateTime(LocalDateTime.now());

        return updateById(notice);
    }

    @Override
    public boolean toggleNoticeStatus(Long id) {
        Notice notice = getById(id);
        if (notice == null) {
            return false;
        }

        notice.setEnabled(notice.getEnabled() == 1 ? 0 : 1);
        notice.setUpdateTime(LocalDateTime.now());

        return updateById(notice);
    }

    @Override
    public boolean deleteNotice(Long id) {
        return removeById(id);
    }

    private NoticeDTO convertToDTO(Notice notice) {
        NoticeDTO dto = new NoticeDTO();
        BeanUtils.copyProperties(notice, dto);
        return dto;
    }
}
