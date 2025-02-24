package com.good.physicalexercisesystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.good.physicalexercisesystem.entity.PhysicalTestRecord;
import com.good.physicalexercisesystem.entity.PhysicalTestItem;
import com.good.physicalexercisesystem.vo.PhysicalTestRecordVO;

import java.util.List;
import java.util.Map;

public interface PhysicalTestService {
    // 获取体测项目列表
    List<PhysicalTestItem> getTestItems();
    
    // 分页获取学生的体测记录
    Page<PhysicalTestRecordVO> getStudentTestRecords(Long studentId, String itemCode, Page<PhysicalTestRecord> page);
    
    // 获取学生的体测统计数据
    Map<String, Object> getStudentTestStatistics(Long studentId);
}