package com.example.usergenerator.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.usergenerator.dto.appointment.AppointmentCreateDTO;
import com.example.usergenerator.dto.appointment.AppointmentQueryDTO;
import com.example.usergenerator.dto.appointment.AppointmentUpdateDTO;
import com.example.usergenerator.entity.Appointment;
import com.example.usergenerator.entity.Department;
import com.example.usergenerator.entity.Doctor;
import com.example.usergenerator.exception.BusinessException;
import com.example.usergenerator.mapper.AppointmentMapper;
import com.example.usergenerator.mapper.DepartmentMapper;
import com.example.usergenerator.mapper.DoctorMapper;
import com.example.usergenerator.service.AppointmentService;
import com.example.usergenerator.vo.appointment.AppointmentVO;
import com.example.usergenerator.vo.appointment.DepartmentVO;
import com.example.usergenerator.vo.appointment.DoctorVO;
import com.example.usergenerator.vo.appointment.TimeSlotVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl extends ServiceImpl<AppointmentMapper, Appointment> implements AppointmentService {

    private final DepartmentMapper departmentMapper;
    private final DoctorMapper doctorMapper;
    private final AppointmentMapper appointmentMapper;

    @Override
    public List<DepartmentVO> getAllDepartments() {
        List<Department> departments = departmentMapper.selectAllDepartments();
        return departments.stream()
                .map(this::convertToDepartmentVO)
                .collect(Collectors.toList());
    }

    @Override
    public DepartmentVO getDepartmentById(Long id) {
        Department department = departmentMapper.selectDepartmentById(id);
        if (department == null) {
            throw new BusinessException("科室不存在");
        }
        return convertToDepartmentVO(department);
    }

    @Override
    public List<DoctorVO> getAllDoctors() {
        List<Doctor> doctors = doctorMapper.selectAllDoctors();
        return doctors.stream()
                .map(this::convertToDoctorVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DoctorVO> getDoctorsByDepartmentId(Long departmentId) {
        List<Doctor> doctors = doctorMapper.selectDoctorsByDepartmentId(departmentId);
        return doctors.stream()
                .map(this::convertToDoctorVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DoctorVO> getDoctorsByDepartmentName(String departmentName) {
        List<Doctor> doctors = doctorMapper.selectDoctorsByDepartmentName(departmentName);
        return doctors.stream()
                .map(this::convertToDoctorVO)
                .collect(Collectors.toList());
    }

    @Override
    public DoctorVO getDoctorById(Long id) {
        Doctor doctor = doctorMapper.selectDoctorWithDepartment(id);
        if (doctor == null) {
            throw new BusinessException("医生不存在");
        }
        return convertToDoctorVO(doctor);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppointmentVO createAppointment(AppointmentCreateDTO dto, Long userId) {
        Doctor doctor = doctorMapper.selectById(dto.getDoctorId());
        if (doctor == null) {
            throw new BusinessException("医生不存在");
        }

        int count = appointmentMapper.countAppointmentsByDoctorDateAndTime(
                dto.getDoctorId(), dto.getAppointmentDate(), dto.getStartTime(), dto.getEndTime());
        if (count > 0) {
            throw new BusinessException("该时间段已被预约");
        }

        Appointment appointment = new Appointment();
        appointment.setUserId(userId);
        appointment.setDoctorId(dto.getDoctorId());
        appointment.setDepartmentId(dto.getDepartmentId());
        appointment.setAppointmentDate(dto.getAppointmentDate());
        appointment.setStartTime(dto.getStartTime());
        appointment.setEndTime(dto.getEndTime());
        appointment.setStatus("pending");
        appointment.setPatientName(dto.getPatientName());
        appointment.setPatientPhone(dto.getPatientPhone());
        appointment.setSymptoms(dto.getSymptoms());

        appointmentMapper.insert(appointment);

        return convertToAppointmentVO(appointment);
    }

    @Override
    public AppointmentVO getAppointmentById(Long id) {
        Appointment appointment = appointmentMapper.selectAppointmentWithDetails(id);
        if (appointment == null) {
            throw new BusinessException("预约不存在");
        }
        return convertToAppointmentVO(appointment);
    }

    @Override
    public IPage<AppointmentVO> getAppointmentsByUserId(AppointmentQueryDTO dto) {
        Page<Appointment> page = new Page<>(dto.getPage(), dto.getSize());
        
        IPage<Appointment> appointmentPage = appointmentMapper.selectPage(page, Wrappers.<Appointment>lambdaQuery()
                .eq(dto.getUserId() != null, Appointment::getUserId, dto.getUserId())
                .eq(dto.getDoctorId() != null, Appointment::getDoctorId, dto.getDoctorId())
                .eq(dto.getDepartmentId() != null, Appointment::getDepartmentId, dto.getDepartmentId())
                .eq(dto.getStatus() != null && !dto.getStatus().isEmpty(), Appointment::getStatus, dto.getStatus())
                .eq(dto.getAppointmentDate() != null, Appointment::getAppointmentDate, dto.getAppointmentDate())
                .orderByDesc(Appointment::getCreateTime));

        return appointmentPage.convert(this::convertToAppointmentVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAppointment(AppointmentUpdateDTO dto) {
        Appointment appointment = appointmentMapper.selectById(dto.getId());
        if (appointment == null) {
            throw new BusinessException("预约不存在");
        }

        if (dto.getStatus() != null) {
            appointment.setStatus(dto.getStatus());
        }
        if (dto.getPatientName() != null) {
            appointment.setPatientName(dto.getPatientName());
        }
        if (dto.getPatientPhone() != null) {
            appointment.setPatientPhone(dto.getPatientPhone());
        }
        if (dto.getSymptoms() != null) {
            appointment.setSymptoms(dto.getSymptoms());
        }

        appointmentMapper.updateById(appointment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelAppointment(Long id) {
        Appointment appointment = appointmentMapper.selectById(id);
        if (appointment == null) {
            throw new BusinessException("预约不存在");
        }

        if ("completed".equals(appointment.getStatus())) {
            throw new BusinessException("已完成的预约无法取消");
        }

        appointment.setStatus("cancelled");
        appointmentMapper.updateById(appointment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initTestData() {
        if (departmentMapper.selectCount(Wrappers.emptyWrapper()) == 0) {
            Department dept1 = new Department();
            dept1.setName("内科");
            dept1.setDescription("内科诊疗");
            dept1.setDoctorsCount(5);
            departmentMapper.insert(dept1);

            Department dept2 = new Department();
            dept2.setName("外科");
            dept2.setDescription("外科诊疗");
            dept2.setDoctorsCount(4);
            departmentMapper.insert(dept2);

            Department dept3 = new Department();
            dept3.setName("儿科");
            dept3.setDescription("儿科诊疗");
            dept3.setDoctorsCount(3);
            departmentMapper.insert(dept3);

            Department dept4 = new Department();
            dept4.setName("妇产科");
            dept4.setDescription("妇产科诊疗");
            dept4.setDoctorsCount(6);
            departmentMapper.insert(dept4);
        }

        if (doctorMapper.selectCount(Wrappers.emptyWrapper()) == 0) {
            Doctor doctor1 = new Doctor();
            doctor1.setName("张医生");
            doctor1.setTitle("主任医师");
            doctor1.setAvatar("");
            doctor1.setRating(4.9);
            doctor1.setConsultationCount(1200);
            doctor1.setSpecialty("心血管、高血压");
            doctor1.setDepartmentId(1L);
            doctorMapper.insert(doctor1);

            Doctor doctor2 = new Doctor();
            doctor2.setName("李医生");
            doctor2.setTitle("副主任医师");
            doctor2.setAvatar("");
            doctor2.setRating(4.8);
            doctor2.setConsultationCount(800);
            doctor2.setSpecialty("糖尿病、内分泌");
            doctor2.setDepartmentId(1L);
            doctorMapper.insert(doctor2);

            Doctor doctor3 = new Doctor();
            doctor3.setName("王医生");
            doctor3.setTitle("主任医师");
            doctor3.setAvatar("");
            doctor3.setRating(4.7);
            doctor3.setConsultationCount(1500);
            doctor3.setSpecialty("骨科、关节");
            doctor3.setDepartmentId(2L);
            doctorMapper.insert(doctor3);

            Doctor doctor4 = new Doctor();
            doctor4.setName("赵医生");
            doctor4.setTitle("主治医师");
            doctor4.setAvatar("");
            doctor4.setRating(4.9);
            doctor4.setConsultationCount(600);
            doctor4.setSpecialty("儿科呼吸、发育");
            doctor4.setDepartmentId(3L);
            doctorMapper.insert(doctor4);

            Doctor doctor5 = new Doctor();
            doctor5.setName("刘医生");
            doctor5.setTitle("主任医师");
            doctor5.setAvatar("");
            doctor5.setRating(4.8);
            doctor5.setConsultationCount(2000);
            doctor5.setSpecialty("妇产科、产前检查");
            doctor5.setDepartmentId(4L);
            doctorMapper.insert(doctor5);
        }

        departmentMapper.selectAllDepartments().forEach(dept -> {
            Long count = doctorMapper.selectCount(Wrappers.<Doctor>lambdaQuery()
                    .eq(Doctor::getDepartmentId, dept.getId()));
            dept.setDoctorsCount(count.intValue());
            departmentMapper.updateById(dept);
        });
    }

    private DepartmentVO convertToDepartmentVO(Department department) {
        return DepartmentVO.builder()
                .id(department.getId())
                .name(department.getName())
                .description(department.getDescription())
                .doctorsCount(department.getDoctorsCount())
                .build();
    }

    private DoctorVO convertToDoctorVO(Doctor doctor) {
        Department department = departmentMapper.selectById(doctor.getDepartmentId());
        
        List<String> specialties = doctor.getSpecialty() != null 
                ? Arrays.asList(doctor.getSpecialty().split("、"))
                : List.of();

        return DoctorVO.builder()
                .doctorId(doctor.getId())
                .name(doctor.getName())
                .title(doctor.getTitle())
                .avatar(doctor.getAvatar())
                .rating(doctor.getRating())
                .consultationCount(doctor.getConsultationCount())
                .specialty(specialties)
                .department(department != null ? convertToDepartmentVO(department) : null)
                .build();
    }

    private AppointmentVO convertToAppointmentVO(Appointment appointment) {
        Doctor doctor = doctorMapper.selectById(appointment.getDoctorId());
        Department department = departmentMapper.selectById(appointment.getDepartmentId());

        TimeSlotVO timeSlot = TimeSlotVO.builder()
                .startTime(appointment.getStartTime())
                .endTime(appointment.getEndTime())
                .build();

        DoctorVO doctorVO = doctor != null ? convertToDoctorVO(doctor) : null;

        return AppointmentVO.builder()
                .appointmentId(appointment.getId())
                .appointmentDate(appointment.getAppointmentDate())
                .timeSlot(timeSlot)
                .doctor(doctorVO)
                .department(department != null ? department.getName() : "")
                .status(appointment.getStatus())
                .patientName(appointment.getPatientName())
                .patientPhone(appointment.getPatientPhone())
                .symptoms(appointment.getSymptoms())
                .build();
    }
}