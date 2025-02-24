package com.good.physicalexercisesystem.controller.student;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.good.physicalexercisesystem.common.ApiResponse;
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
    public ApiResponse<List<PhysicalTestItem>> getTestItems() {
        return ApiResponse.success(physicalTestService.getTestItems());
    }

    @GetMapping("/records")
    public ApiResponse<Page<PhysicalTestRecordVO>> getTestRecords(
            Authentication authentication,
            @RequestParam(required = false) String itemCode,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        User user = userService.findByUsername(authentication.getName());
        Page<PhysicalTestRecord> page = new Page<>(current, size);
        return ApiResponse.success(
            physicalTestService.getStudentTestRecords(user.getId(), itemCode, page)
        );
    }

    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getTestStatistics(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        return ApiResponse.success(
            physicalTestService.getStudentTestStatistics(user.getId())
        );
    }
}
