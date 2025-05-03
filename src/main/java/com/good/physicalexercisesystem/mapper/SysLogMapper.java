// Mapper接口
package com.good.physicalexercisesystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.good.physicalexercisesystem.entity.SysLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysLogMapper extends BaseMapper<SysLog> {
    @Select("SELECT * FROM sys_log WHERE deleted = 0 ORDER BY create_time DESC LIMIT 4")
    List<SysLog> getLatestLogs();
}
