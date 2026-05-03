package com.example.usergenerator.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.usergenerator.entity.VisitRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VisitRecordMapper extends BaseMapper<VisitRecord> {
    List<VisitRecord> selectByUserId(@Param("userId") Long userId);
}