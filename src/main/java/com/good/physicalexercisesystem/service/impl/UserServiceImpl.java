package com.good.physicalexercisesystem.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.good.physicalexercisesystem.common.exception.CustomException;
import com.good.physicalexercisesystem.dto.UpdatePasswordDTO;
import com.good.physicalexercisesystem.entity.*;
import com.good.physicalexercisesystem.mapper.*;
import com.good.physicalexercisesystem.service.UserService;
import com.good.physicalexercisesystem.utils.JwtUtils;
import com.good.physicalexercisesystem.utils.UserContext;
import com.good.physicalexercisesystem.vo.UserProfileVo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import com.good.physicalexercisesystem.dto.UpdateProfileDTO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import com.good.physicalexercisesystem.utils.MinioUtils;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final MinioUtils minioUtils;
    private final StudentInfoMapper studentInfoMapper;
    private final TeacherInfoMapper teacherInfoMapper;
    private final PeStudentClassMapper peStudentClassMapper;
    private final PeClassMapper peClassMapper;

    @Override
    public User findByUsername(String username) {
        // 查询启用状态的用户
        return getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .eq(User::getEnabled, 1));
    }

    @Override
    public UserProfileVo getProfile(Long id) {
        // 查询启用状态的用户
        User one = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getId, id)
                .eq(User::getEnabled, 1));
        if (one == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        UserProfileVo result = BeanUtil.toBean(one, UserProfileVo.class);
        String userType = one.getUserType();
        if (userType.equals("student")) {
            // 联查userInfo表填充数据
            LambdaQueryWrapper<StudentInfo> wrapper = new LambdaQueryWrapper<StudentInfo>()
                    .eq(StudentInfo::getUserId, one.getId());
            StudentInfo studentInfo = studentInfoMapper.selectOne(wrapper);
            result.setStudentId(Integer.valueOf(studentInfo.getStudentId()));
            result.setClassName(studentInfo.getClassName());
        } else if (userType.equals("teacher")) {
            LambdaQueryWrapper<TeacherInfo> wrapper = new LambdaQueryWrapper<TeacherInfo>()
                    .eq(TeacherInfo::getUserId, one.getId());
            TeacherInfo teacherInfo = teacherInfoMapper.selectOne(wrapper);
            result.setTeacherCode(teacherInfo.getTeacherCode());
        } else if (userType.equals("admin")) {
            // TODO

            return result;
        }
        return result;
    }

    @Override
    public void register(User user) {
        // 检查用户名唯一性
        if (findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 密码加密存储
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true); // 设置为启用状态

        // 保存用户信息
        save(user);
    }

    @Override
    public String login(String username, String password, String userType) {
        // 查询用户信息
        User user = findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        // 验证用户类型
        if (!user.getUserType().equals(userType)) {
            throw new BadCredentialsException("用户类型不匹配");
        }

        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("密码错误");
        }

        // 生成JWT令牌
        return jwtUtils.generateToken(user);
    }

    @Override
    public void resetPassword(String username, String oldPassword, String newPassword) {
        // 查询用户信息
        User user = findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证原密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }

        // 更新新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        updateById(user);
    }

    @Override
    @Transactional
    public void updateProfile(Long userId, UpdateProfileDTO profileDTO) {
        // 获取并校验用户是否存在
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 更新基本信息
        user.setName(profileDTO.getName());
        user.setGender(profileDTO.getGender());
        user.setPhone(profileDTO.getPhone());
        user.setEmail(profileDTO.getEmail());
        updateById(user);

        // 更新额外信息
        if (user.getUserType().equals("student")) {
            // 更新班级信息
            if (profileDTO.getClassId() != null) {
                // 先查询是否在关系中，否则是要新增
                PeStudentClass peStudentClass = peStudentClassMapper.selectOne(
                        new LambdaQueryWrapper<PeStudentClass>()
                                .eq(PeStudentClass::getStudentId, UserContext.getUser().getId())
                );
                if (peStudentClass == null) {
                    peStudentClass = new PeStudentClass();
                    peStudentClass.setStudentId(UserContext.getUser().getId());
                    peStudentClass.setClassId(Long.valueOf(profileDTO.getClassId()));
                    peStudentClassMapper.insert(peStudentClass);
                } else {
                    LambdaUpdateWrapper<PeStudentClass> updateWrapper = new LambdaUpdateWrapper<PeStudentClass>()
                            .eq(PeStudentClass::getStudentId, UserContext.getUser().getId())
                            .set(PeStudentClass::getClassId, profileDTO.getClassId());
                    peStudentClassMapper.update(null, updateWrapper);
                }
            }
            // 班级信息
            PeClass peClass = peClassMapper.selectOne(
                    new LambdaQueryWrapper<PeClass>()
                            .eq(PeClass::getId, profileDTO.getClassId())
            );
            LambdaUpdateWrapper<StudentInfo> wrapper = new LambdaUpdateWrapper<StudentInfo>()
                    .eq(StudentInfo::getUserId, user.getId())
                    .set(profileDTO.getStudentId() != null, StudentInfo::getStudentId, profileDTO.getStudentId())
                    .set(peClass != null, StudentInfo::getClassName, peClass.getClassName());
            studentInfoMapper.update(null, wrapper);

        } else if (user.getUserType().equals("teacher")) {
            LambdaUpdateWrapper<TeacherInfo> wrapper = new LambdaUpdateWrapper<TeacherInfo>()
                    .eq(TeacherInfo::getUserId, user.getId())
                    .set(profileDTO.getTeacherCode() != null, TeacherInfo::getTeacherCode, profileDTO.getTeacherCode());
            log.info("更新的信息: {}", profileDTO);
            teacherInfoMapper.update(null, wrapper);
        } else if (user.getUserType().equals("admin")) {
            // TODO
        }
    }

    @Override
    public String uploadAvatar(Long userId, MultipartFile file) {
        // 文件为空校验
        if (file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }

        // 文件类型校验
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.startsWith("image/jpeg") && !contentType.startsWith("image/png"))) {
            throw new RuntimeException("只支持JPG/PNG格式的图片");
        }

        // 文件大小校验(2MB)
        if (file.getSize() > 2 * 1024 * 1024) {
            throw new RuntimeException("文件大小不能超过2MB");
        }

        try {
            // 生成文件名
            String fileName = "avatar/" + userId + "/" + UUID.randomUUID() + getFileExtension(file.getOriginalFilename());

            // 上传到MinIO
            String fileUrl = minioUtils.uploadFile(file, fileName);

            // 更新用户头像信息
            User user = getById(userId);
            // 如果用户之前有头像，删除旧头像
            if (user.getAvatar() != null) {
                String oldFileName = user.getAvatar().substring(user.getAvatar().indexOf("avatar/"));
                minioUtils.deleteFile(oldFileName);
            }
            user.setAvatar(fileUrl);
            updateById(user);

            return fileUrl;
        } catch (Exception e) {
            throw new RuntimeException("头像上传失败", e);
        }
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName 原始文件名
     * @return 扩展名(包含.)
     */
    private String getFileExtension(String fileName) {
        return fileName != null && fileName.contains(".")
                ? fileName.substring(fileName.lastIndexOf("."))
                : ".jpg";
    }

    @Override
    public void updatePassword(UpdatePasswordDTO passwordDTO) {
        User user = UserContext.getUser();
        // 查询用户信息
        if (user == null) {
            throw new CustomException("用户不存在");
        }

        // 验证原密码
        if (!passwordEncoder.matches(passwordDTO.getOldPassword(), user.getPassword())) {
            throw new CustomException("原密码错误");
        }

        // 更新新密码
        user.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
        updateById(user);
    }
}
