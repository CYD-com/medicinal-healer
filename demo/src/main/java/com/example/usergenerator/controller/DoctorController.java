package com.example.usergenerator.controller;

import com.example.usergenerator.annotation.RequirePermission;
import com.example.usergenerator.common.Result;
import com.example.usergenerator.constant.RoleConstants;
import com.example.usergenerator.entity.Department;
import com.example.usergenerator.entity.Doctor;
import com.example.usergenerator.mapper.DepartmentMapper;
import com.example.usergenerator.mapper.DoctorMapper;
import com.example.usergenerator.vo.appointment.DepartmentVO;
import com.example.usergenerator.vo.appointment.DoctorVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @GetMapping("/list")
    @RequirePermission(RoleConstants.ADMIN)
    public Result<List<DoctorVO>> listDoctors() {
        List<Doctor> doctors = doctorMapper.selectAllDoctors();
        List<DoctorVO> voList = doctors.stream().map(d -> {
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
        }).collect(Collectors.toList());
        return Result.success("查询成功", voList);
    }

    @PostMapping("/create")
    @RequirePermission(RoleConstants.ADMIN)
    public Result<DoctorVO> createDoctor(@RequestBody Doctor doctor) {
        doctorMapper.insert(doctor);
        return Result.success("创建成功", convertToVO(doctor));
    }

    @PutMapping("/update")
    @RequirePermission(RoleConstants.ADMIN)
    public Result<Void> updateDoctor(@RequestBody Doctor doctor) {
        Doctor existing = doctorMapper.selectById(doctor.getId());
        if (existing == null) {
            return Result.error(404, "医生不存在");
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
