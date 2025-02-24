package com.good.physicalexercisesystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.good.physicalexercisesystem.entity.PhysicalTestItem;
import com.good.physicalexercisesystem.entity.PhysicalTestRecord;
import com.good.physicalexercisesystem.mapper.PhysicalTestItemMapper;
import com.good.physicalexercisesystem.mapper.PhysicalTestRecordMapper;
import com.good.physicalexercisesystem.service.PhysicalTestService;
import com.good.physicalexercisesystem.vo.PhysicalTestRecordVO;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PhysicalTestServiceImpl implements PhysicalTestService {

    private final PhysicalTestItemMapper testItemMapper;
    private final PhysicalTestRecordMapper testRecordMapper;

    public PhysicalTestServiceImpl(PhysicalTestItemMapper testItemMapper, 
                                 PhysicalTestRecordMapper testRecordMapper) {
        this.testItemMapper = testItemMapper;
        this.testRecordMapper = testRecordMapper;
    }

    @Override
    public List<PhysicalTestItem> getTestItems() {
        return testItemMapper.selectList(
            new LambdaQueryWrapper<PhysicalTestItem>()
                .eq(PhysicalTestItem::getEnabled, 1)
                .orderByAsc(PhysicalTestItem::getId)
        );
    }

    @Override
    public Page<PhysicalTestRecordVO> getStudentTestRecords(Long studentId, String itemCode, Page<PhysicalTestRecord> page) {
        // 构建查询条件
        LambdaQueryWrapper<PhysicalTestRecord> wrapper = new LambdaQueryWrapper<PhysicalTestRecord>()
            .eq(PhysicalTestRecord::getStudentId, studentId)
            .eq(itemCode != null && !itemCode.isEmpty(), PhysicalTestRecord::getTestItemId, 
                Optional.ofNullable(itemCode)
                    .map(code -> testItemMapper.selectOne(
                        new LambdaQueryWrapper<PhysicalTestItem>()
                            .eq(PhysicalTestItem::getItemCode, code)
                    ))
                    .map(PhysicalTestItem::getId)
                    .orElse(null)
            )
            .orderByDesc(PhysicalTestRecord::getTestDate);

        // 执行分页查询
        Page<PhysicalTestRecord> recordPage = testRecordMapper.selectPage(page, wrapper);

        // 转换为VO
        Page<PhysicalTestRecordVO> voPage = new Page<>(
            recordPage.getCurrent(),
            recordPage.getSize(),
            recordPage.getTotal()  // 使用 MyBatis-Plus 返回的总记录数
        );

        // 转换记录
        List<PhysicalTestRecordVO> voList = recordPage.getRecords().stream()
            .map(record -> {
                PhysicalTestRecordVO vo = PhysicalTestRecordVO.from(record);
                PhysicalTestItem item = testItemMapper.selectById(record.getTestItemId());
                if (item != null) {
                    vo.setItemName(item.getItemName());
                    vo.setItemCode(item.getItemCode());
                    vo.setUnit(item.getUnit());
                }
                return vo;
            })
            .collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public Map<String, Object> getStudentTestStatistics(Long studentId) {
        Map<String, Object> statistics = new HashMap<>();
        
        // 获取最新的测试记录
        PhysicalTestRecord latestRecord = testRecordMapper.selectOne(
            new LambdaQueryWrapper<PhysicalTestRecord>()
                .eq(PhysicalTestRecord::getStudentId, studentId)
                .orderByDesc(PhysicalTestRecord::getTestDate)
                .last("LIMIT 1")
        );
        
        if (latestRecord != null) {
            statistics.put("latestScore", latestRecord.getScore());
            statistics.put("latestTestDate", latestRecord.getTestDate());
            
            // 获取项目名称
            PhysicalTestItem item = testItemMapper.selectById(latestRecord.getTestItemId());
            if (item != null) {
                statistics.put("latestTestType", item.getItemName());
            }
        }

        // 计算平均分
        Double avgScore = testRecordMapper.selectObjs(
            new LambdaQueryWrapper<PhysicalTestRecord>()
                .select(PhysicalTestRecord::getScore)
                .eq(PhysicalTestRecord::getStudentId, studentId)
        ).stream()
            .mapToInt(obj -> Integer.parseInt(obj.toString()))
            .average()
            .orElse(0.0);
        
        statistics.put("averageScore", avgScore);

        // 获取测试总次数
        Long totalTests = testRecordMapper.selectCount(
            new LambdaQueryWrapper<PhysicalTestRecord>()
                .eq(PhysicalTestRecord::getStudentId, studentId)
        );
        statistics.put("totalTests", totalTests);

        // 获取及格次数（分数>=60）
        Long passedTests = testRecordMapper.selectCount(
            new LambdaQueryWrapper<PhysicalTestRecord>()
                .eq(PhysicalTestRecord::getStudentId, studentId)
                .ge(PhysicalTestRecord::getScore, 60)
        );
        statistics.put("passedTests", passedTests);

        return statistics;
    }
} 