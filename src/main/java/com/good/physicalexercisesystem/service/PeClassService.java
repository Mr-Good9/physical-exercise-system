package com.good.physicalexercisesystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.good.physicalexercisesystem.entity.PeClass;
import com.good.physicalexercisesystem.dto.ClassDTO;

/**
 * 班级服务接口
 */
public interface PeClassService extends IService<PeClass> {
    IPage<ClassDTO> getClassList(Long teacherId, Integer page, Integer pageSize);

    ClassDTO getClassDetail(Long id);

    boolean addClass(PeClass peClass);

    boolean updateClass(PeClass peClass);

    boolean deleteClass(Long id);


}
