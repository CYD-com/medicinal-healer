package com.example.usergenerator.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.usergenerator.entity.HealthRecordAuthorization;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HealthRecordAuthorizationMapper extends BaseMapper<HealthRecordAuthorization> {
    List<HealthRecordAuthorization> selectByUserId(@Param("userId") Long userId);
}