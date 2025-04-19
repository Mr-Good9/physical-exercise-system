package com.good.physicalexercisesystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.good.physicalexercisesystem.common.CommonResult;
import com.good.physicalexercisesystem.dto.NoticeDTO;
import com.good.physicalexercisesystem.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @GetMapping("/list")
    public CommonResult<Page<NoticeDTO>> getNoticePage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String type) {

        Page<NoticeDTO> result = noticeService.getNoticePage(page, pageSize, title, type);
        return CommonResult.success(result);
    }

    @PostMapping("/add")
    public CommonResult<Boolean> addNotice(@RequestBody @Validated NoticeDTO noticeDTO) {
        boolean success = noticeService.addNotice(noticeDTO);
        return success ? CommonResult.success(true) : CommonResult.error("添加通知失败");
    }

    @PutMapping("/update/{id}")
    public CommonResult<Boolean> updateNotice(@PathVariable Long id, @RequestBody @Validated NoticeDTO noticeDTO) {
        boolean success = noticeService.updateNotice(id, noticeDTO);
        return success ? CommonResult.success(true) : CommonResult.error("更新通知失败");
    }

    @PutMapping("/toggle/{id}")
    public CommonResult<Boolean> toggleNoticeStatus(@PathVariable Long id) {
        boolean success = noticeService.toggleNoticeStatus(id);
        return success ? CommonResult.success(true) : CommonResult.error("切换通知状态失败");
    }

    @DeleteMapping("/delete/{id}")
    public CommonResult<Boolean> deleteNotice(@PathVariable Long id) {
        boolean success = noticeService.deleteNotice(id);
        return success ? CommonResult.success(true) : CommonResult.error("删除通知失败");
    }
}
