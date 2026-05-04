package com.example.usergenerator.service;

import com.example.usergenerator.dto.schedule.DoctorScheduleDTO;
import com.example.usergenerator.dto.schedule.ScheduleQueryDTO;
import com.example.usergenerator.entity.DoctorSchedule;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface DoctorScheduleService {

    DoctorSchedule createSchedule(DoctorScheduleDTO dto);

    List<DoctorSchedule> getSchedulesByDoctor(Long doctorId, LocalDate startDate, LocalDate endDate);

    List<DoctorSchedule> getSchedulesByDepartment(Long departmentId, LocalDate workDate);

    Map<String, Object> getScheduleList(ScheduleQueryDTO query);

    boolean updateStatus(Long id, Integer status);

    boolean deleteSchedule(Long id);

    boolean checkAvailability(Long doctorId, LocalDate workDate, String timeSlot);

    List<Map<String, Object>> getAvailableTimeSlots(Long doctorId, LocalDate workDate);
}
