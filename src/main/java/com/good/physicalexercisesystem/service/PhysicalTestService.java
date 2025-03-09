package com.good.physicalexercisesystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.good.physicalexercisesystem.entity.PhysicalTestRecord;
import com.good.physicalexercisesystem.dto.PhysicalTestRecordDTO;
import com.good.physicalexercisesystem.dto.PhysicalTestQuery;
import com.good.physicalexercisesystem.entity.PhysicalTestItem;
import com.good.physicalexercisesystem.vo.PhysicalTestRecordVO;

import java.util.List;
import java.util.Map;

public interface PhysicalTestService extends IService<PhysicalTestRecord> {
    /**
     * 获取体测项目列表
     *
     * @return
     */
    List<PhysicalTestItem> getTestItems();

    /**
     * 分页获取学生的体测记录
     *
     * @param page
     * @return
     */
    IPage<PhysicalTestRecordDTO> getTestRecordList(Integer page, Integer pageSize, PhysicalTestQuery query);

    /**
     * 获取学生的体测统计数据
     *
     * @param studentId
     * @return
     */
    Map<String, Object> getStudentTestStatistics(Long studentId);

    /**
     * 获取学生体测记录
     *
     * @param studentId
     * @return
     */
    List<PhysicalTestRecordVO> getStudentTests(Long studentId);

    boolean updateTestScore(Long id, Integer score, String evaluation);

    boolean updateTestComment(Long id, String teacherComment);

    Page<PhysicalTestRecordVO> getStudentTestRecords (Long studentId,String itemCode, Page<PhysicalTestRecord> page);
}
