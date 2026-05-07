package com.example.usergenerator.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.usergenerator.entity.Prescription;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PrescriptionMapper extends BaseMapper<Prescription> {

    List<Prescription> selectByUserId(@Param("userId") Long userId);

    List<Prescription> selectByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);

    List<Prescription> selectByDoctorId(@Param("doctorId") Long doctorId);

    List<Prescription> selectByDoctorIdAndStatus(@Param("doctorId") Long doctorId, @Param("status") String status);
}
