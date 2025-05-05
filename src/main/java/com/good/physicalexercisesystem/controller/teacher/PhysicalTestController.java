package com.good.physicalexercisesystem.controller.teacher;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.good.physicalexercisesystem.annotation.Log;
import com.good.physicalexercisesystem.common.CommonResult;
import com.good.physicalexercisesystem.entity.PhysicalTestItem;
import com.good.physicalexercisesystem.dto.PhysicalTestRecordDTO;
import com.good.physicalexercisesystem.dto.PhysicalTestQuery;
import com.good.physicalexercisesystem.service.PhysicalTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@RestController("TeacherPhysicalTestController")
@RequestMapping("/physical")
public class PhysicalTestController {

    @Autowired
    private PhysicalTestService physicalTestService;

    /**
     * 获取测试项目列表
     * @return
     */
    @GetMapping("/items")
    public CommonResult<List<PhysicalTestItem>> getTestItems() {
        return CommonResult.success(physicalTestService.getTestItems());
    }

    /**
     * 获取测试记录列表
     * @param page
     * @param pageSize
     * @param query
     * @return
     */
    @GetMapping("/records")
    public CommonResult<IPage<PhysicalTestRecordDTO>> getTestRecords(
            @RequestParam(defaultValue = "1", name = "page") Integer page,
            @RequestParam(defaultValue = "10", name = "pageSize") Integer pageSize,
            PhysicalTestQuery query) {
        return CommonResult.success(physicalTestService.getTestRecordList(page, pageSize, query));
    }

    /**
     * 更新测试成绩
     * @param id
     * @param score
     * @param evaluation
     * @return
     */
    @PutMapping("/records/{id}/score")
    @Log("更新测试成绩")
    public CommonResult<Void> updateTestScore(
            @PathVariable Long id,
            @RequestParam Integer score,
            @RequestParam String evaluation) {
        physicalTestService.updateTestScore(id, score, evaluation);
        return CommonResult.success(null);
    }

    /**
     * 更新测试评价
     * @param id
     * @param teacherComment
     * @return
     */
    @PutMapping("/records/{id}/comment")
    @Log("更新测试评价")
    public CommonResult<Void> updateTestComment(
            @PathVariable Long id,
            @RequestParam String teacherComment) {
        physicalTestService.updateTestComment(id, teacherComment);
        return CommonResult.success(null);
    }

    @PostMapping("/records")
    @Log("新增测试记录")
    public CommonResult<Void> addTestRecord(@RequestBody @Validated PhysicalTestRecordDTO recordDTO) {
        // 获取当前登录用户
//        Long teacherId = UserContext.getCurrentUserId();
        // 根据分数自动生成评价
        if (recordDTO.getScore() != null && (recordDTO.getEvaluation() == null || recordDTO.getEvaluation().isEmpty())) {
            recordDTO.setEvaluation(generateEvaluation(recordDTO.getScore()));
        }
        // 保存记录
        physicalTestService.addTestRecord(recordDTO);
        return CommonResult.success(null);
    }

    /**
     * 根据分数生成评价等级
     */
    private String generateEvaluation(Integer score) {
        if (score >= 90) return "优秀";
        if (score >= 80) return "良好";
        if (score >= 60) return "及格";
        return "不及格";
    }

}
