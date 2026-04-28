package com.example.usergenerator.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.usergenerator.entity.Appointment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface AppointmentMapper extends BaseMapper<Appointment> {

    List<Appointment> selectAppointmentsByUserId(@Param("userId") Long userId);

    List<Appointment> selectAppointmentsByDoctorId(@Param("doctorId") Long doctorId);

    List<Appointment> selectAppointmentsByStatus(@Param("status") String status);

    List<Appointment> selectAppointmentsByDate(@Param("appointmentDate") LocalDate appointmentDate);

    Appointment selectAppointmentWithDetails(@Param("id") Long id);

    int countAppointmentsByDoctorAndDate(@Param("doctorId") Long doctorId, @Param("appointmentDate") LocalDate appointmentDate);

    int countAppointmentsByDoctorDateAndTime(@Param("doctorId") Long doctorId, 
                                             @Param("appointmentDate") LocalDate appointmentDate,
                                             @Param("startTime") String startTime,
                                             @Param("endTime") String endTime);
}