package com.good.physicalexercisesystem.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.good.physicalexercisesystem.entity.PeClass;
import com.good.physicalexercisesystem.dto.ClassDTO;
import com.good.physicalexercisesystem.mapper.PeClassMapper;
import com.good.physicalexercisesystem.service.PeClassService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 班级服务实现类
 */
@Slf4j
@Service
public class PeClassServiceImpl extends ServiceImpl<PeClassMapper, PeClass> implements PeClassService {

    @Override
    public IPage<ClassDTO> getClassList(Long teacherId, Integer page, Integer pageSize) {
        Page<ClassDTO> pageParam = new Page<>(page, pageSize);
        log.info("getClassList: teacherId={}, page={}, pageSize={}", teacherId, page, pageSize);
        return baseMapper.selectClassList(pageParam, teacherId);
    }

    @Override
    public ClassDTO getClassDetail(Long id) {
        Page<ClassDTO> page = new Page<>(1, 1);
        page.setSearchCount(false);
        IPage<ClassDTO> result = baseMapper.selectClassList(page, null);
        return result.getRecords().isEmpty() ? null : result.getRecords().get(0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addClass(PeClass peClass) {
        return save(peClass);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateClass(PeClass peClass) {
        return updateById(peClass);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteClass(Long id) {
        // 检查是否有学生
        Integer studentCount = baseMapper.countStudentsByClassId(id);
        if (studentCount > 0) {
            throw new RuntimeException("该班级还有学生，不能删除");
        }
        return removeById(id);
    }
}
