package com.example.usergenerator.converter;

import com.example.usergenerator.dto.user.UserRegisterDTO;
import com.example.usergenerator.entity.SysUser;
import com.example.usergenerator.vo.user.UserGenerateVO;
import com.example.usergenerator.vo.user.UserVO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserConverter {

    UserVO toVO(SysUser user);

    List<UserVO> toVOList(List<SysUser> users);

    UserGenerateVO toGenerateVO(SysUser user);

    SysUser toEntity(UserRegisterDTO dto);
}
