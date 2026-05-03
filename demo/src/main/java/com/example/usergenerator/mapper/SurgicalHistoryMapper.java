package com.example.usergenerator.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.usergenerator.entity.SurgicalHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SurgicalHistoryMapper extends BaseMapper<SurgicalHistory> {
    List<SurgicalHistory> selectByUserId(@Param("userId") Long userId);
}