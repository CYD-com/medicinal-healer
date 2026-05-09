package com.example.usergenerator.controller;

import com.example.usergenerator.annotation.RequirePermission;
import com.example.usergenerator.common.Result;
import com.example.usergenerator.constant.RoleConstants;
import com.example.usergenerator.entity.Appointment;
import com.example.usergenerator.entity.Consultation;
import com.example.usergenerator.entity.Department;
import com.example.usergenerator.entity.Doctor;
import com.example.usergenerator.entity.DoctorSchedule;
import com.example.usergenerator.entity.Prescription;
import com.example.usergenerator.entity.SysUser;
import com.example.usergenerator.mapper.AppointmentMapper;
import com.example.usergenerator.mapper.ConsultationMapper;
import com.example.usergenerator.mapper.DepartmentMapper;
import com.example.usergenerator.mapper.DoctorMapper;
import com.example.usergenerator.mapper.DoctorScheduleMapper;
import com.example.usergenerator.mapper.PrescriptionMapper;
import com.example.usergenerator.mapper.SysUserMapper;
import com.example.usergenerator.vo.appointment.DepartmentVO;
import com.example.usergenerator.vo.appointment.DoctorVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/doctor")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorMapper doctorMapper;
    private final DepartmentMapper departmentMapper;
    private final SysUserMapper sysUserMapper;
    private final AppointmentMapper appointmentMapper;
    private final ConsultationMapper consultationMapper;
    private final PrescriptionMapper prescriptionMapper;
    private final DoctorScheduleMapper doctorScheduleMapper;

    @GetMapping("/list")
    @RequirePermission(RoleConstants.ADMIN)
    public Result<IPage<DoctorVO>> listDoctors(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name) {
        if (StringUtils.hasText(name)) {
            LambdaQueryWrapper<SysUser> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.like(SysUser::getRealName, name).eq(SysUser::getRole, "doctor");
            List<SysUser> matchedUsers = sysUserMapper.selectList(userWrapper);
            if (matchedUsers.isEmpty()) {
                return Result.success("查询成功", new Page<DoctorVO>().setRecords(List.of()).setTotal(0));
            }
            List<Long> userIds = matchedUsers.stream().map(SysUser::getId).collect(Collectors.toList());
            LambdaQueryWrapper<Doctor> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(Doctor::getUserId, userIds);
            wrapper.orderByAsc(Doctor::getId);
            Page<Doctor> pageParam = new Page<>(page, size);
            IPage<Doctor> doctorPage = doctorMapper.selectPage(pageParam, wrapper);
            List<Long> doctorIds = doctorPage.getRecords().stream().map(Doctor::getId).collect(Collectors.toList());
            List<Doctor> doctorsWithUser = doctorIds.isEmpty() ? List.of() : doctorMapper.selectDoctorsWithUserByIds(doctorIds);
            java.util.Map<Long, Doctor> doctorMap = doctorsWithUser.stream()
                    .collect(Collectors.toMap(Doctor::getId, d -> d));
            IPage<DoctorVO> voPage = doctorPage.convert(d -> convertToVO(doctorMap.getOrDefault(d.getId(), d)));
            return Result.success("查询成功", voPage);
        }
        Page<Doctor> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Doctor> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Doctor::getId);
        IPage<Doctor> doctorPage = doctorMapper.selectPage(pageParam, wrapper);
        List<Long> doctorIds = doctorPage.getRecords().stream().map(Doctor::getId).collect(Collectors.toList());
        List<Doctor> doctorsWithUser = doctorIds.isEmpty() ? List.of() : doctorMapper.selectDoctorsWithUserByIds(doctorIds);
        java.util.Map<Long, Doctor> doctorMap = doctorsWithUser.stream()
                .collect(Collectors.toMap(Doctor::getId, d -> d));
        IPage<DoctorVO> voPage = doctorPage.convert(d -> convertToVO(doctorMap.getOrDefault(d.getId(), d)));
        return Result.success("查询成功", voPage);
    }

    @PostMapping("/create")
    @RequirePermission(RoleConstants.ADMIN)
    public Result<DoctorVO> createDoctor(@RequestBody Doctor doctor) {
        if (doctor.getUserId() == null) {
            SysUser newUser = new SysUser();
            newUser.setUsername("doctor_" + System.currentTimeMillis());
            newUser.setPassword("$2b$10$uRAS68xyAueDj1s/o0UZjO.k4JOW1.QWWwajImr/d1RiFx2z9l47y");
            newUser.setRealName(doctor.getName() != null ? doctor.getName() : "新医生");
            newUser.setGender("未知");
            newUser.setAge(0);
            newUser.setPhone("");
            newUser.setEmail("");
            newUser.setIdCard("");
            newUser.setAddress("");
            newUser.setRole("doctor");
            newUser.setStatus(1);
            sysUserMapper.insert(newUser);
            doctor.setUserId(newUser.getId());
        } else {
            SysUser user = sysUserMapper.selectById(doctor.getUserId());
            if (user != null && doctor.getName() != null) {
                user.setRealName(doctor.getName());
                sysUserMapper.updateById(user);
            }
        }
        doctorMapper.insert(doctor);
        Doctor saved = doctorMapper.selectDoctorWithUserById(doctor.getId());
        return Result.success("创建成功", convertToVO(saved));
    }

    @PutMapping("/update")
    @RequirePermission(RoleConstants.ADMIN)
    public Result<Void> updateDoctor(@RequestBody Doctor doctor) {
        Doctor existing = doctorMapper.selectById(doctor.getId());
        if (existing == null) {
            return Result.error(404, "医生不存在");
        }
        if (existing.getUserId() != null && doctor.getName() != null) {
            SysUser user = sysUserMapper.selectById(existing.getUserId());
            if (user != null) {
                user.setRealName(doctor.getName());
                sysUserMapper.updateById(user);
            }
        }
        doctorMapper.updateById(doctor);
        return Result.success("更新成功");
    }

    @DeleteMapping("/delete/{id}")
    @RequirePermission(RoleConstants.ADMIN)
    public Result<Void> deleteDoctor(@PathVariable Long id) {
        Doctor existing = doctorMapper.selectById(id);
        if (existing == null) {
            return Result.error(404, "医生不存在");
        }

        List<String> relatedTables = new java.util.ArrayList<>();

        if (appointmentMapper.selectCount(
                new LambdaQueryWrapper<com.example.usergenerator.entity.Appointment>()
                        .eq(com.example.usergenerator.entity.Appointment::getDoctorId, id)) > 0) {
            relatedTables.add("预约记录");
        }
        if (consultationMapper.selectCount(
                new LambdaQueryWrapper<com.example.usergenerator.entity.Consultation>()
                        .eq(com.example.usergenerator.entity.Consultation::getDoctorId, id)) > 0) {
            relatedTables.add("问诊记录");
        }
        if (prescriptionMapper.selectCount(
                new LambdaQueryWrapper<com.example.usergenerator.entity.Prescription>()
                        .eq(com.example.usergenerator.entity.Prescription::getDoctorId, id)) > 0) {
            relatedTables.add("处方记录");
        }
        if (doctorScheduleMapper.selectCount(
                new LambdaQueryWrapper<com.example.usergenerator.entity.DoctorSchedule>()
                        .eq(com.example.usergenerator.entity.DoctorSchedule::getDoctorId, id)) > 0) {
            relatedTables.add("排班记录");
        }

        if (!relatedTables.isEmpty()) {
            return Result.error(400, "该医生存在关联数据（" + String.join("、", relatedTables) + "），无法删除");
        }

        doctorMapper.deleteById(id);
        return Result.success("删除成功");
    }

    private DoctorVO convertToVO(Doctor d) {
        Department dept = departmentMapper.selectById(d.getDepartmentId());
        DepartmentVO deptVO = dept != null ? DepartmentVO.builder()
                .id(dept.getId())
                .name(dept.getName())
                .description(dept.getDescription())
                .doctorsCount(dept.getDoctorsCount())
                .build() : null;
        List<String> specialties = d.getSpecialty() != null
                ? Arrays.asList(d.getSpecialty().split("、"))
                : List.of();
        return DoctorVO.builder()
                .doctorId(d.getId())
                .name(d.getName())
                .title(d.getTitle())
                .avatar(d.getAvatar())
                .rating(d.getRating())
                .consultationCount(d.getConsultationCount())
                .specialty(specialties)
                .department(deptVO)
                .userId(d.getUserId())
                .build();
    }
}
