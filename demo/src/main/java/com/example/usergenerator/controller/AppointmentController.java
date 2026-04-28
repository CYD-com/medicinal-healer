package com.example.usergenerator.controller;

import com.example.usergenerator.annotation.RequirePermission;
import com.example.usergenerator.annotation.RepeatSubmit;
import com.example.usergenerator.common.Result;
import com.example.usergenerator.constant.RoleConstants;
import com.example.usergenerator.dto.appointment.AppointmentCreateDTO;
import com.example.usergenerator.dto.appointment.AppointmentQueryDTO;
import com.example.usergenerator.dto.appointment.AppointmentUpdateDTO;
import com.example.usergenerator.service.AppointmentService;
import com.example.usergenerator.util.PermissionUtil;
import com.example.usergenerator.vo.appointment.AppointmentVO;
import com.example.usergenerator.vo.appointment.DepartmentVO;
import com.example.usergenerator.vo.appointment.DoctorVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/appointment")
@RequiredArgsConstructor
@Validated
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping("/departments")
    public Result<List<DepartmentVO>> getDepartments() {
        List<DepartmentVO> departments = appointmentService.getAllDepartments();
        return Result.success("查询成功", departments);
    }

    @GetMapping("/departments/{id}")
    public Result<DepartmentVO> getDepartmentById(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        DepartmentVO department = appointmentService.getDepartmentById(id);
        return Result.success("查询成功", department);
    }

    @GetMapping("/doctors")
    public Result<List<DoctorVO>> getDoctors(
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String departmentName) {
        List<DoctorVO> doctors;
        if (departmentName != null && !departmentName.isEmpty()) {
            doctors = appointmentService.getDoctorsByDepartmentName(departmentName);
        } else if (departmentId != null) {
            doctors = appointmentService.getDoctorsByDepartmentId(departmentId);
        } else {
            doctors = appointmentService.getAllDoctors();
        }
        return Result.success("查询成功", doctors);
    }

    @GetMapping("/doctors/{id}")
    public Result<DoctorVO> getDoctorById(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        DoctorVO doctor = appointmentService.getDoctorById(id);
        return Result.success("查询成功", doctor);
    }

    @PostMapping("/create")
    @RequirePermission({RoleConstants.USER, RoleConstants.ADMIN})
    @RepeatSubmit(timeout = 3000)
    public Result<AppointmentVO> createAppointment(@Valid @RequestBody AppointmentCreateDTO dto) {
        Long userId = PermissionUtil.getCurrentUserId();
        AppointmentVO appointment = appointmentService.createAppointment(dto, userId);
        return Result.success("预约成功", appointment);
    }

    @GetMapping("/list")
    @RequirePermission({RoleConstants.USER, RoleConstants.ADMIN})
    public Result<IPage<AppointmentVO>> getAppointments(
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        AppointmentQueryDTO dto = new AppointmentQueryDTO();
        dto.setUserId(PermissionUtil.getCurrentUserId());
        dto.setDoctorId(doctorId);
        dto.setDepartmentId(departmentId);
        dto.setStatus(status);
        dto.setPage(page);
        dto.setSize(size);
        
        IPage<AppointmentVO> appointments = appointmentService.getAppointmentsByUserId(dto);
        return Result.success("查询成功", appointments);
    }

    @GetMapping("/{id}")
    @RequirePermission({RoleConstants.USER, RoleConstants.ADMIN})
    public Result<AppointmentVO> getAppointmentById(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        AppointmentVO appointment = appointmentService.getAppointmentById(id);
        return Result.success("查询成功", appointment);
    }

    @PutMapping("/update/{id}")
    @RequirePermission({RoleConstants.USER, RoleConstants.ADMIN})
    @RepeatSubmit(timeout = 3000)
    public Result<Void> updateAppointment(@PathVariable Long id,
                                           @Valid @RequestBody AppointmentUpdateDTO dto) {
        dto.setId(id);
        appointmentService.updateAppointment(dto);
        return Result.success("更新成功");
    }

    @PutMapping("/cancel/{id}")
    @RequirePermission({RoleConstants.USER, RoleConstants.ADMIN})
    public Result<Void> cancelAppointment(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        appointmentService.cancelAppointment(id);
        return Result.success("取消成功");
    }

    @GetMapping("/init")
    public Result<Void> initData() {
        appointmentService.initTestData();
        return Result.success("初始化成功");
    }
}