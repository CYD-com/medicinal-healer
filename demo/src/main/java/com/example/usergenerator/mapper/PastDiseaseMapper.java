package com.example.usergenerator.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.usergenerator.entity.PastDisease;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PastDiseaseMapper extends BaseMapper<PastDisease> {
    List<PastDisease> selectByUserId(@Param("userId") Long userId);
}