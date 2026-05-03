package com.example.usergenerator.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.usergenerator.entity.HealthRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface HealthRecordMapper extends BaseMapper<HealthRecord> {
    HealthRecord selectByUserId(@Param("userId") Long userId);
}