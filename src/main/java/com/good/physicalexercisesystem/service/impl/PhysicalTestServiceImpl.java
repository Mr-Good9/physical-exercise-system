package com.good.physicalexercisesystem.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.good.physicalexercisesystem.entity.PhysicalTestItem;
import com.good.physicalexercisesystem.entity.PhysicalTestRecord;
import com.good.physicalexercisesystem.dto.PhysicalTestRecordDTO;
import com.good.physicalexercisesystem.dto.PhysicalTestQuery;
import com.good.physicalexercisesystem.mapper.PhysicalTestItemMapper;
import com.good.physicalexercisesystem.mapper.PhysicalTestRecordMapper;
import com.good.physicalexercisesystem.service.PhysicalTestService;
import com.good.physicalexercisesystem.vo.PhysicalTestRecordVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PhysicalTestServiceImpl extends ServiceImpl<PhysicalTestRecordMapper, PhysicalTestRecord> implements PhysicalTestService {

    @Autowired
    private PhysicalTestItemMapper testItemMapper;

    @Override
    public IPage<PhysicalTestRecordDTO> getTestRecordList(Integer page, Integer pageSize, PhysicalTestQuery query) {
        // 创建分页对象，注意页码从1开始
        Page<PhysicalTestRecordDTO> pageParam = new Page<>(page, pageSize);
        // 调用Mapper的分页查询方法
        return baseMapper.selectTestRecordList(pageParam, query);
    }

    @Override
    public List<PhysicalTestItem> getTestItems() {
        return testItemMapper.selectList(
            new LambdaQueryWrapper<PhysicalTestItem>()
                .eq(PhysicalTestItem::getEnabled, true)
                .orderByAsc(PhysicalTestItem::getItemCode)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTestScore(Long id, Integer score, String evaluation) {
        PhysicalTestRecord record = new PhysicalTestRecord();
        record.setId(id);
        record.setScore(score);
        record.setEvaluation(evaluation);
        return updateById(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTestComment(Long id, String teacherComment) {
        PhysicalTestRecord record = new PhysicalTestRecord();
        record.setId(id);
        record.setTeacherComment(teacherComment);
        return updateById(record);
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
        Page<PhysicalTestRecord> recordPage = baseMapper.selectPage(page, wrapper);

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
        PhysicalTestRecord latestRecord = baseMapper.selectOne(
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
        Double avgScore = baseMapper.selectObjs(
                        new LambdaQueryWrapper<PhysicalTestRecord>()
                                .select(PhysicalTestRecord::getScore)
                                .eq(PhysicalTestRecord::getStudentId, studentId)
                ).stream()
                .mapToInt(obj -> Integer.parseInt(obj.toString()))
                .average()
                .orElse(0.0);

        statistics.put("averageScore", avgScore);

        // 获取测试总次数
        Long totalTests = baseMapper.selectCount(
                new LambdaQueryWrapper<PhysicalTestRecord>()
                        .eq(PhysicalTestRecord::getStudentId, studentId)
        );
        statistics.put("totalTests", totalTests);

        // 获取及格次数（分数>=60）
        Long passedTests = baseMapper.selectCount(
                new LambdaQueryWrapper<PhysicalTestRecord>()
                        .eq(PhysicalTestRecord::getStudentId, studentId)
                        .ge(PhysicalTestRecord::getScore, 60)
        );
        statistics.put("passedTests", passedTests);

        return statistics;
    }

    /**
     * 用于展示到dashboard
     * 获取10条最新的学生测试记录
     *
     * @param studentId
     * @return
     */
    @Override
    public List<PhysicalTestRecordVO> getStudentTests(Long studentId) {
        // 测试记录
        LambdaQueryWrapper<PhysicalTestRecord> wrapper = new LambdaQueryWrapper<PhysicalTestRecord>()
                .eq(PhysicalTestRecord::getStudentId, studentId)
                .orderByDesc(PhysicalTestRecord::getTestDate).last("LIMIT 10");
        List<PhysicalTestRecord> physicalTestRecords = baseMapper.selectList(wrapper);
        // 获取测试记录的项目id
         List<Long> testItemIds = physicalTestRecords.stream()
                .map(PhysicalTestRecord::getTestItemId)
                .collect(Collectors.toList());
        List<PhysicalTestItem> physicalTestItems = testItemMapper.selectBatchIds(testItemIds);
        // 构造成k-v的map，k是项目id，v是项目信息
        Map<Long, PhysicalTestItem> testRecordMap = physicalTestItems.stream().collect(Collectors.toMap(PhysicalTestItem::getId, u -> u));

        List<PhysicalTestRecordVO> testRecordVO = BeanUtil.copyToList(physicalTestRecords, PhysicalTestRecordVO.class);
        // 联查获取项目信息
        for (PhysicalTestRecordVO record : testRecordVO) {
            PhysicalTestItem physicalTestRecord = testRecordMap.get(record.getTestItemId());
            record.setItemName(physicalTestRecord.getItemName());
            record.setItemCode(physicalTestRecord.getItemCode());
            record.setUnit(physicalTestRecord.getUnit());
        }
        return testRecordVO;
    }
}
