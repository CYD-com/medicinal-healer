package com.example.usergenerator.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.usergenerator.entity.Doctor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DoctorMapper extends BaseMapper<Doctor> {

    List<Doctor> selectAllDoctors();

    List<Doctor> selectDoctorsByDepartmentId(@Param("departmentId") Long departmentId);

    List<Doctor> selectDoctorsByDepartmentName(@Param("departmentName") String departmentName);

    Doctor selectDoctorWithDepartment(@Param("doctorId") Long doctorId);

    Doctor selectByUserId(@Param("userId") Long userId);
}