package com.example.usergenerator.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.usergenerator.common.ResultCode;
import com.example.usergenerator.constant.RoleConstants;
import com.example.usergenerator.converter.UserConverter;
import com.example.usergenerator.dto.user.AdminAddUserDTO;
import com.example.usergenerator.dto.user.AdminUserUpdateDTO;
import com.example.usergenerator.dto.user.ChangePasswordDTO;
import com.example.usergenerator.dto.user.UpdateProfileDTO;
import com.example.usergenerator.dto.user.UserLoginDTO;
import com.example.usergenerator.dto.user.UserRegisterDTO;
import com.example.usergenerator.dto.user.UserUpdateDTO;
import com.example.usergenerator.dto.user.UserUpdateRoleDTO;
import com.example.usergenerator.entity.SysUser;
import com.example.usergenerator.exception.BusinessException;
import com.example.usergenerator.mapper.SysUserMapper;
import com.example.usergenerator.service.CaptchaService;
import com.example.usergenerator.service.SysUserService;
import com.example.usergenerator.util.JwtUtil;
import com.example.usergenerator.util.UserGenerator;
import com.example.usergenerator.vo.user.UserGenerateVO;
import com.example.usergenerator.vo.user.UserLoginVO;
import com.example.usergenerator.vo.user.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final UserConverter userConverter;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final CaptchaService captchaService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserGenerateVO generateAndSaveUser() {
        SysUser user = UserGenerator.generateUser();
        String plainPassword = user.getPassword();
        user.setPassword(passwordEncoder.encode(plainPassword));
        this.save(user);
        log.info("生成并保存用户成功，ID：{}", user.getId());
        return UserGenerateVO.builder()
                .username(user.getUsername())
                .password(plainPassword)
                .role(user.getRole())
                .build();
    }

    @Override
    public UserLoginVO login(UserLoginDTO dto) {
        SysUser user = getUserEntityByUsername(dto.getUsername());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.INVALID_CREDENTIALS);
        }

        if (dto.getLoginRole() != null && !dto.getLoginRole().isEmpty()
                && !dto.getLoginRole().equals(user.getRole())) {
            throw new BusinessException(ResultCode.ROLE_MISMATCH);
        }

        String token = generateToken(user);
        UserVO userVO = userConverter.toVO(user);

        log.info("用户登录成功，用户名：{}", dto.getUsername());
        return UserLoginVO.builder()
                .token(token)
                .role(user.getRole())
                .user(userVO)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(UserRegisterDTO dto) {
        boolean captchaValid = captchaService.verifyCaptcha(dto.getUuid(), dto.getCode());
        if (!captchaValid) {
            throw new BusinessException(ResultCode.INVALID_CAPTCHA);
        }

        SysUser existingUser = getUserEntityByUsername(dto.getUsername());
        if (existingUser != null) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS);
        }

        SysUser user = userConverter.toEntity(dto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (StrUtil.isBlank(user.getRole())) {
            user.setRole(RoleConstants.USER);
        }
        this.save(user);

        log.info("用户注册成功，用户名：{}", dto.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO adminAddUser(AdminAddUserDTO dto) {
        SysUser existingUser = getUserEntityByUsername(dto.getUsername());
        if (existingUser != null) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS);
        }

        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        if (StrUtil.isNotBlank(dto.getRole())) {
            user.setRole(dto.getRole());
        } else {
            user.setRole(RoleConstants.USER);
        }
        if (StrUtil.isNotBlank(dto.getRealName())) {
            user.setRealName(dto.getRealName());
        }
        if (StrUtil.isNotBlank(dto.getGender())) {
            user.setGender(dto.getGender());
        }
        if (dto.getAge() != null) {
            user.setAge(dto.getAge());
        }
        if (StrUtil.isNotBlank(dto.getPhone())) {
            user.setPhone(dto.getPhone());
        }
        if (StrUtil.isNotBlank(dto.getEmail())) {
            user.setEmail(dto.getEmail());
        }
        if (StrUtil.isNotBlank(dto.getIdCard())) {
            user.setIdCard(dto.getIdCard());
        }
        if (StrUtil.isNotBlank(dto.getAddress())) {
            user.setAddress(dto.getAddress());
        }
        if (dto.getStatus() != null) {
            user.setStatus(dto.getStatus());
        }
        this.save(user);
        log.info("管理员添加用户成功，用户名：{}", dto.getUsername());
        return userConverter.toVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserRole(UserUpdateRoleDTO dto) {
        SysUser user = this.getById(dto.getId());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        user.setRole(dto.getRole());
        this.updateById(user);

        log.info("更新用户角色成功，ID：{}", dto.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        SysUser user = this.getById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        this.removeById(id);
        log.info("删除用户成功，ID：{}", id);
    }

    @Override
    public UserVO getUserById(Long id) {
        SysUser user = this.getById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return userConverter.toVO(user);
    }

    @Override
    public UserVO getUserByUsername(String username) {
        SysUser user = getUserEntityByUsername(username);
        if (user == null) {
            return null;
        }
        return userConverter.toVO(user);
    }

    @Override
    public List<UserVO> listAllUsers() {
        List<SysUser> users = this.list();
        return userConverter.toVOList(users);
    }

    private String generateToken(SysUser user) {
        return jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
    }

    private SysUser getUserEntityByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        return this.getOne(wrapper);
    }

    private Long getCurrentUserId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        String token = attributes.getRequest().getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            token = attributes.getRequest().getHeader("X-Token");
        }
        Long userId = jwtUtil.getUserId(token);
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return userId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAvatar(String avatar) {
        Long userId = getCurrentUserId();
        SysUser user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        user.setAvatar(avatar);
        this.updateById(user);
        log.info("更新用户头像成功，ID：{}", userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserInfo(UserUpdateDTO dto) {
        Long userId = getCurrentUserId();
        SysUser user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        if (StrUtil.isNotBlank(dto.getRealName())) {
            user.setRealName(dto.getRealName());
        }
        if (StrUtil.isNotBlank(dto.getGender())) {
            user.setGender(dto.getGender());
        }
        if (dto.getAge() != null) {
            user.setAge(dto.getAge());
        }
        if (StrUtil.isNotBlank(dto.getPhone())) {
            user.setPhone(dto.getPhone());
        }
        if (StrUtil.isNotBlank(dto.getEmail())) {
            user.setEmail(dto.getEmail());
        }
        if (StrUtil.isNotBlank(dto.getIdCard())) {
            user.setIdCard(dto.getIdCard());
        }
        if (StrUtil.isNotBlank(dto.getAddress())) {
            user.setAddress(dto.getAddress());
        }
        if (StrUtil.isNotBlank(dto.getAvatar())) {
            user.setAvatar(dto.getAvatar());
        }

        this.updateById(user);
        log.info("更新用户信息成功，ID：{}", userId);
    }

    @Override
    public UserVO getCurrentUserInfo() {
        Long userId = getCurrentUserId();
        SysUser user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return userConverter.toVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(ChangePasswordDTO dto) {
        Long userId = getCurrentUserId();
        SysUser user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        this.updateById(user);
        log.info("修改密码成功，用户ID：{}", userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProfile(UpdateProfileDTO dto) {
        Long userId = getCurrentUserId();
        SysUser user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        if (StrUtil.isNotBlank(dto.getRealName())) {
            user.setRealName(dto.getRealName());
        }
        if (StrUtil.isNotBlank(dto.getGender())) {
            user.setGender(dto.getGender());
        }
        if (dto.getAge() != null) {
            user.setAge(dto.getAge());
        }
        if (StrUtil.isNotBlank(dto.getPhone())) {
            user.setPhone(dto.getPhone());
        }
        if (StrUtil.isNotBlank(dto.getEmail())) {
            user.setEmail(dto.getEmail());
        }
        if (StrUtil.isNotBlank(dto.getAddress())) {
            user.setAddress(dto.getAddress());
        }
        if (StrUtil.isNotBlank(dto.getAvatar())) {
            user.setAvatar(dto.getAvatar());
        }

        this.updateById(user);
        log.info("更新个人资料成功，用户ID：{}", userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO adminUpdateUser(AdminUserUpdateDTO dto) {
        SysUser user = this.getById(dto.getId());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        if (StrUtil.isNotBlank(dto.getRealName())) {
            user.setRealName(dto.getRealName());
        }
        if (StrUtil.isNotBlank(dto.getGender())) {
            user.setGender(dto.getGender());
        }
        if (dto.getAge() != null) {
            user.setAge(dto.getAge());
        }
        if (StrUtil.isNotBlank(dto.getPhone())) {
            user.setPhone(dto.getPhone());
        }
        if (StrUtil.isNotBlank(dto.getEmail())) {
            user.setEmail(dto.getEmail());
        }
        if (StrUtil.isNotBlank(dto.getIdCard())) {
            user.setIdCard(dto.getIdCard());
        }
        if (StrUtil.isNotBlank(dto.getAddress())) {
            user.setAddress(dto.getAddress());
        }
        if (StrUtil.isNotBlank(dto.getRole())) {
            user.setRole(dto.getRole());
        }
        if (dto.getStatus() != null) {
            user.setStatus(dto.getStatus());
        }

        this.updateById(user);
        log.info("管理员更新用户信息成功，用户ID：{}", dto.getId());
        return userConverter.toVO(user);
    }
}
