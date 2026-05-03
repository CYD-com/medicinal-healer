package com.example.usergenerator.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.usergenerator.entity.HealthIndicator;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HealthIndicatorMapper extends BaseMapper<HealthIndicator> {
    List<HealthIndicator> selectByUserIdAndType(@Param("userId") Long userId, @Param("indicatorType") String indicatorType);
}