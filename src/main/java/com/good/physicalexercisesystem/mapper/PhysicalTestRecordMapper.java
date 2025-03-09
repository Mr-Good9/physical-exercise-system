package com.good.physicalexercisesystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.good.physicalexercisesystem.entity.PhysicalTestRecord;
import com.good.physicalexercisesystem.dto.PhysicalTestRecordDTO;
import com.good.physicalexercisesystem.dto.PhysicalTestQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PhysicalTestRecordMapper extends BaseMapper<PhysicalTestRecord> {
    IPage<PhysicalTestRecordDTO> selectTestRecordList(IPage<PhysicalTestRecordDTO> page, @Param("query") PhysicalTestQuery query);
} 