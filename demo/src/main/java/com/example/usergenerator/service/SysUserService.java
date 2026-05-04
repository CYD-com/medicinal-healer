package com.example.usergenerator.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.usergenerator.dto.user.UserLoginDTO;
import com.example.usergenerator.dto.user.UserRegisterDTO;
import com.example.usergenerator.dto.user.ChangePasswordDTO;
import com.example.usergenerator.dto.user.UpdateProfileDTO;
import com.example.usergenerator.dto.user.UserUpdateDTO;
import com.example.usergenerator.dto.user.AdminAddUserDTO;
import com.example.usergenerator.dto.user.AdminUserUpdateDTO;
import com.example.usergenerator.dto.user.UserUpdateRoleDTO;
import com.example.usergenerator.entity.SysUser;
import com.example.usergenerator.vo.user.UserGenerateVO;
import com.example.usergenerator.vo.user.UserLoginVO;
import com.example.usergenerator.vo.user.UserVO;

import java.util.List;

public interface SysUserService extends IService<SysUser> {

    UserGenerateVO generateAndSaveUser();

    UserLoginVO login(UserLoginDTO dto);

    void register(UserRegisterDTO dto);

    UserVO adminAddUser(AdminAddUserDTO dto);

    void updateUserRole(UserUpdateRoleDTO dto);

    UserVO getUserById(Long id);

    UserVO getUserByUsername(String username);

    List<UserVO> listAllUsers();

    void deleteUser(Long id);

    void updateAvatar(String avatar);

    void updateUserInfo(UserUpdateDTO dto);

    UserVO getCurrentUserInfo();

    void changePassword(ChangePasswordDTO dto);

    void updateProfile(UpdateProfileDTO dto);

    UserVO adminUpdateUser(AdminUserUpdateDTO dto);
}
