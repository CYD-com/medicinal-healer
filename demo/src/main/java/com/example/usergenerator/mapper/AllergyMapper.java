package com.example.usergenerator.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.usergenerator.entity.Allergy;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AllergyMapper extends BaseMapper<Allergy> {
    List<Allergy> selectByUserId(@Param("userId") Long userId);
}