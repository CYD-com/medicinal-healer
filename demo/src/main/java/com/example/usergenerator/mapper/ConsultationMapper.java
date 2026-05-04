package com.example.usergenerator.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.usergenerator.entity.Consultation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ConsultationMapper extends BaseMapper<Consultation> {

    List<Consultation> selectByUserId(@Param("userId") Long userId);

    List<Consultation> selectByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);

    List<Consultation> selectByDoctorId(@Param("doctorId") Long doctorId);

    List<Consultation> selectByDoctorIdAndStatus(@Param("doctorId") Long doctorId, @Param("status") String status);
}