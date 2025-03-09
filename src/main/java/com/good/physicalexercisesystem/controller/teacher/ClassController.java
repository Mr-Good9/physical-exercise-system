package com.good.physicalexercisesystem.controller.teacher;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.good.physicalexercisesystem.common.CommonResult;
import com.good.physicalexercisesystem.entity.PeClass;
import com.good.physicalexercisesystem.dto.ClassDTO;
import com.good.physicalexercisesystem.dto.ClassForm;
import com.good.physicalexercisesystem.service.PeClassService;
import com.good.physicalexercisesystem.utils.UserContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/teacher/classes")
public class ClassController {

    @Autowired
    private PeClassService peClassService;

    @GetMapping
    public CommonResult<IPage<ClassDTO>> getClassList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long teacherId = UserContext.getUser().getId();
        return CommonResult.success(peClassService.getClassList(teacherId, page, pageSize));
    }

    @GetMapping("/{id}")
    public CommonResult<ClassDTO> getClassDetail(@PathVariable Long id) {
        return CommonResult.success(peClassService.getClassDetail(id));
    }

    @PostMapping
    public CommonResult<Boolean> addClass(@RequestBody ClassForm form) {
        PeClass peClass = new PeClass();
        BeanUtils.copyProperties(form, peClass);
        peClass.setTeacherId(Objects.requireNonNull(UserContext.getUser()).getId());
        return CommonResult.success(peClassService.addClass(peClass));
    }

    @PutMapping("/{id}")
    public CommonResult<Boolean> updateClass(@PathVariable Long id, @RequestBody ClassForm form) {
        PeClass peClass = new PeClass();
        BeanUtils.copyProperties(form, peClass);
        peClass.setId(id);
        return CommonResult.success(peClassService.updateClass(peClass));
    }

    @DeleteMapping("/{id}")
    public CommonResult<Boolean> deleteClass(@PathVariable Long id) {
        return CommonResult.success(peClassService.deleteClass(id));
    }
}
