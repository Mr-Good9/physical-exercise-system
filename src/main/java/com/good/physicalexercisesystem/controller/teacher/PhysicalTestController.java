package com.good.physicalexercisesystem.controller.teacher;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.good.physicalexercisesystem.common.CommonResult;
import com.good.physicalexercisesystem.entity.PhysicalTestItem;
import com.good.physicalexercisesystem.dto.PhysicalTestRecordDTO;
import com.good.physicalexercisesystem.dto.PhysicalTestQuery;
import com.good.physicalexercisesystem.service.PhysicalTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("TeacherPhysicalTestController")
@RequestMapping("/physical")
public class PhysicalTestController {

    @Autowired
    private PhysicalTestService physicalTestService;

    @GetMapping("/items")
    public CommonResult<List<PhysicalTestItem>> getTestItems() {
        return CommonResult.success(physicalTestService.getTestItems());
    }

    @GetMapping("/records")
    public CommonResult<IPage<PhysicalTestRecordDTO>> getTestRecords(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            PhysicalTestQuery query) {
        return CommonResult.success(physicalTestService.getTestRecordList(page, pageSize, query));
    }

    @PutMapping("/records/{id}/score")
    public CommonResult<Void> updateTestScore(
            @PathVariable Long id,
            @RequestParam Integer score,
            @RequestParam String evaluation) {
        physicalTestService.updateTestScore(id, score, evaluation);
        return CommonResult.success(null);
    }

    @PutMapping("/records/{id}/comment")
    public CommonResult<Void> updateTestComment(
            @PathVariable Long id,
            @RequestParam String teacherComment) {
        physicalTestService.updateTestComment(id, teacherComment);
        return CommonResult.success(null);
    }
}
