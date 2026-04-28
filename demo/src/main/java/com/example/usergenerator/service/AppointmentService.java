package com.example.usergenerator.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.usergenerator.dto.appointment.AppointmentCreateDTO;
import com.example.usergenerator.dto.appointment.AppointmentQueryDTO;
import com.example.usergenerator.dto.appointment.AppointmentUpdateDTO;
import com.example.usergenerator.entity.Appointment;
import com.example.usergenerator.entity.Department;
import com.example.usergenerator.entity.Doctor;
import com.example.usergenerator.vo.appointment.AppointmentVO;
import com.example.usergenerator.vo.appointment.DepartmentVO;
import com.example.usergenerator.vo.appointment.DoctorVO;

import java.util.List;

public interface AppointmentService extends IService<Appointment> {

    List<DepartmentVO> getAllDepartments();

    DepartmentVO getDepartmentById(Long id);

    List<DoctorVO> getAllDoctors();

    List<DoctorVO> getDoctorsByDepartmentId(Long departmentId);

    List<DoctorVO> getDoctorsByDepartmentName(String departmentName);

    DoctorVO getDoctorById(Long id);

    AppointmentVO createAppointment(AppointmentCreateDTO dto, Long userId);

    AppointmentVO getAppointmentById(Long id);

    IPage<AppointmentVO> getAppointmentsByUserId(AppointmentQueryDTO dto);

    void updateAppointment(AppointmentUpdateDTO dto);

    void cancelAppointment(Long id);

    void initTestData();
}