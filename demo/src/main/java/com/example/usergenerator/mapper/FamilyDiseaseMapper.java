package com.example.usergenerator.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.usergenerator.entity.FamilyDisease;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FamilyDiseaseMapper extends BaseMapper<FamilyDisease> {
    List<FamilyDisease> selectByUserId(@Param("userId") Long userId);
}