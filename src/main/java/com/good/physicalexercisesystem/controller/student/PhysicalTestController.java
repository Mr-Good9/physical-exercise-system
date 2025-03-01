package com.good.physicalexercisesystem.controller.student;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.good.physicalexercisesystem.common.CommonResult;
import com.good.physicalexercisesystem.entity.PhysicalTestItem;
import com.good.physicalexercisesystem.entity.PhysicalTestRecord;
import com.good.physicalexercisesystem.entity.User;
import com.good.physicalexercisesystem.service.PhysicalTestService;
import com.good.physicalexercisesystem.service.UserService;
import com.good.physicalexercisesystem.vo.PhysicalTestRecordVO;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/student/physical-test")
public class PhysicalTestController {

    private final PhysicalTestService physicalTestService;
    private final UserService userService;

    public PhysicalTestController(PhysicalTestService physicalTestService, UserService userService) {
        this.physicalTestService = physicalTestService;
        this.userService = userService;
    }

    @GetMapping("/items")
    public CommonResult<List<PhysicalTestItem>> getTestItems() {
        return CommonResult.success(physicalTestService.getTestItems());
    }

    @GetMapping("/records")
    public CommonResult<Page<PhysicalTestRecordVO>> getTestRecords(
            Authentication authentication,
            @RequestParam(required = false) String itemCode,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        User user = userService.findByUsername(authentication.getName());
        Page<PhysicalTestRecord> page = new Page<>(current, size);
        return CommonResult.success(
            physicalTestService.getStudentTestRecords(user.getId(), itemCode, page)
        );
    }

    @GetMapping("/statistics")
    public CommonResult<Map<String, Object>> getTestStatistics(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        return CommonResult.success(
            physicalTestService.getStudentTestStatistics(user.getId())
        );
    }
}
