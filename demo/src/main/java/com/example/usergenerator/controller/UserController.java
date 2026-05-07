package com.example.usergenerator.controller;

import com.example.usergenerator.annotation.RequirePermission;
import com.example.usergenerator.annotation.RepeatSubmit;
import com.example.usergenerator.common.Result;
import com.example.usergenerator.constant.RoleConstants;
import com.example.usergenerator.dto.user.AdminAddUserDTO;
import com.example.usergenerator.dto.user.AdminUserUpdateDTO;
import com.example.usergenerator.dto.user.UserLoginDTO;
import com.example.usergenerator.dto.user.UserRegisterDTO;
import com.example.usergenerator.dto.user.UserUpdateDTO;
import com.example.usergenerator.dto.user.UserUpdateRoleDTO;
import com.example.usergenerator.dto.user.AvatarUpdateDTO;
import com.example.usergenerator.dto.user.ChangePasswordDTO;
import com.example.usergenerator.dto.user.UpdateProfileDTO;
import com.example.usergenerator.service.SysUserService;
import com.example.usergenerator.util.TokenBlacklistUtil;
import com.example.usergenerator.vo.user.UserGenerateVO;
import com.example.usergenerator.vo.user.UserLoginVO;
import com.example.usergenerator.vo.user.UserVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final SysUserService sysUserService;
    private final TokenBlacklistUtil tokenBlacklistUtil;

    @PostMapping("/generate")
    public Result<UserGenerateVO> generateUser() {
        UserGenerateVO vo = sysUserService.generateAndSaveUser();
        return Result.success("用户生成成功", vo);
    }

    @PostMapping("/login")
    public Result<UserLoginVO> login(@Valid @RequestBody UserLoginDTO dto) {
        UserLoginVO vo = sysUserService.login(dto);
        return Result.success("登录成功", vo);
    }
    
    @PostMapping("/login/form")
    public Result<UserLoginVO> loginForm(@RequestParam String username, 
                                        @RequestParam String password) {
        UserLoginDTO dto = new UserLoginDTO();
        dto.setUsername(username);
        dto.setPassword(password);
        UserLoginVO vo = sysUserService.login(dto);
        return Result.success("登录成功", vo);
    }

    @PostMapping("/register")
    @RepeatSubmit(timeout = 3000)
    public Result<Void> register(@Valid @RequestBody UserRegisterDTO dto) {
        sysUserService.register(dto);
        return Result.success("注册成功");
    }

    @PostMapping("/add")
    @RequirePermission(RoleConstants.ADMIN)
    @RepeatSubmit(timeout = 3000)
    public Result<UserVO> addUser(@Valid @RequestBody AdminAddUserDTO dto) {
        UserVO userVO = sysUserService.adminAddUser(dto);
        return Result.success("添加成功", userVO);
    }

    @GetMapping("/list")
    @RequirePermission({RoleConstants.USER, RoleConstants.DOCTOR, RoleConstants.ADMIN})
    public Result<IPage<UserVO>> getUserList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        IPage<UserVO> userList = sysUserService.listUsersByPage(page, size, keyword);
        return Result.success("查询成功", userList);
    }

    @GetMapping("/detail/{id}")
    @RequirePermission(RoleConstants.ADMIN)
    public Result<UserVO> getUserDetail(@PathVariable @NotNull(message = "用户ID不能为空") Long id) {
        UserVO userVO = sysUserService.getUserById(id);
        return Result.success("查询成功", userVO);
    }

    @DeleteMapping("/delete/{id}")
    @RequirePermission(RoleConstants.ADMIN)
    public Result<Void> deleteUser(@PathVariable @NotNull(message = "用户ID不能为空") Long id) {
        sysUserService.deleteUser(id);
        return Result.success("删除成功");
    }

    @PutMapping("/role")
    @RequirePermission(RoleConstants.ADMIN)
    public Result<UserVO> updateUserRole(@Valid @RequestBody UserUpdateRoleDTO dto) {
        sysUserService.updateUserRole(dto);
        UserVO userVO = sysUserService.getUserById(dto.getId());
        return Result.success("角色修改成功", userVO);
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        // 从请求头中提取 token
        String token = extractToken(request);
        if (token != null && !token.isEmpty()) {
            // 将 token 加入黑名单
            tokenBlacklistUtil.addToBlacklist(token);
            log.info("用户退出登录，token 已加入黑名单");
        }
        return Result.success("退出登录成功");
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return request.getHeader("X-Token");
    }

    @PostMapping("/avatar")
    @RequirePermission({RoleConstants.USER, RoleConstants.DOCTOR, RoleConstants.ADMIN})
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error(400, "文件不能为空");
        }

        // 确保上传目录存在
        String uploadDir = System.getProperty("user.dir") + "/uploads/avatar";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + fileExtension;
        String filePath = uploadDir + "/" + fileName;

        try {
            // 保存文件
            file.transferTo(new File(filePath));
            // 返回相对路径
            String avatarUrl = "/uploads/avatar/" + fileName;
            return Result.success("上传成功", avatarUrl);
        } catch (IOException e) {
            log.error("上传头像失败", e);
            return Result.error(500, "上传失败");
        }
    }

    @PutMapping("/avatar")
    @RequirePermission({RoleConstants.USER, RoleConstants.DOCTOR, RoleConstants.ADMIN})
    public Result<Void> updateAvatar(@RequestBody AvatarUpdateDTO dto) {
        sysUserService.updateAvatar(dto.getAvatar());
        return Result.success("更新成功");
    }

    @GetMapping("/info")
    @RequirePermission({RoleConstants.USER, RoleConstants.DOCTOR, RoleConstants.ADMIN})
    public Result<UserVO> getCurrentUserInfo() {
        UserVO userVO = sysUserService.getCurrentUserInfo();
        return Result.success("查询成功", userVO);
    }

    @PutMapping("/info")
    @RequirePermission({RoleConstants.USER, RoleConstants.DOCTOR, RoleConstants.ADMIN})
    public Result<Void> updateUserInfo(@Valid @RequestBody UserUpdateDTO dto) {
        sysUserService.updateUserInfo(dto);
        return Result.success("更新成功");
    }

    @PutMapping("/password")
    @RequirePermission({RoleConstants.USER, RoleConstants.DOCTOR, RoleConstants.ADMIN})
    @RepeatSubmit(timeout = 3000)
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordDTO dto) {
        sysUserService.changePassword(dto);
        return Result.success("密码修改成功");
    }

    @PutMapping("/profile")
    @RequirePermission({RoleConstants.USER, RoleConstants.DOCTOR, RoleConstants.ADMIN})
    public Result<Void> updateProfile(@Valid @RequestBody UpdateProfileDTO dto) {
        sysUserService.updateProfile(dto);
        return Result.success("资料更新成功");
    }

    @PutMapping("/update/{id}")
    @RequirePermission(RoleConstants.ADMIN)
    public Result<UserVO> adminUpdateUser(@PathVariable Long id, @Valid @RequestBody AdminUserUpdateDTO dto) {
        dto.setId(id);
        UserVO userVO = sysUserService.adminUpdateUser(dto);
        return Result.success("更新成功", userVO);
    }
}
