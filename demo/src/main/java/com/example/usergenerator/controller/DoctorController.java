package com.example.usergenerator.controller;

import com.example.usergenerator.annotation.RequirePermission;
import com.example.usergenerator.common.Result;
import com.example.usergenerator.constant.RoleConstants;
import com.example.usergenerator.entity.Department;
import com.example.usergenerator.entity.Doctor;
import com.example.usergenerator.entity.SysUser;
import com.example.usergenerator.mapper.DepartmentMapper;
import com.example.usergenerator.mapper.DoctorMapper;
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
        if (doctor.getUserId() != null && doctor.getName() != null) {
            SysUser user = sysUserMapper.selectById(doctor.getUserId());
            if (user != null) {
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
