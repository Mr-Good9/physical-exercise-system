package com.good.physicalexercisesystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.good.physicalexercisesystem.entity.TeacherInfo;
import com.good.physicalexercisesystem.mapper.TeacherInfoMapper;
import com.good.physicalexercisesystem.service.ITeacherInfoService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 教师信息扩展表 服务实现类
 * </p>
 *
 * @author chris
 * @since 2025-03-09
 */
@Service
public class TeacherInfoServiceImpl extends ServiceImpl<TeacherInfoMapper, TeacherInfo> implements ITeacherInfoService {

}
