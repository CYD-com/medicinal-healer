package com.example.usergenerator.controller;

import com.example.usergenerator.annotation.RequirePermission;
import com.example.usergenerator.common.Result;
import com.example.usergenerator.constant.RoleConstants;
import com.example.usergenerator.entity.Consultation;
import com.example.usergenerator.service.AppointmentService;
import com.example.usergenerator.service.ConsultationService;
import com.example.usergenerator.service.DrugService;
import com.example.usergenerator.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final SysUserService userService;
    private final AppointmentService appointmentService;
    private final ConsultationService consultationService;
    private final DrugService drugService;

    @GetMapping("/overview")
    @RequirePermission(RoleConstants.ADMIN)
    public Result<Map<String, Object>> getOverview() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userService.count());
        stats.put("totalDoctors", appointmentService.getAllDoctors().size());
        stats.put("totalDepartments", appointmentService.getAllDepartments().size());

        long totalAppointments = appointmentService.count();
        stats.put("totalAppointments", totalAppointments);

        long pendingConsultations = consultationService.count(
                new LambdaQueryWrapper<Consultation>().eq(Consultation::getStatus, "pending"));
        long inProgressConsultations = consultationService.count(
                new LambdaQueryWrapper<Consultation>().eq(Consultation::getStatus, "in_progress"));
        long completedConsultations = consultationService.count(
                new LambdaQueryWrapper<Consultation>().eq(Consultation::getStatus, "completed"));

        stats.put("pendingConsultations", pendingConsultations);
        stats.put("inProgressConsultations", inProgressConsultations);
        stats.put("completedConsultations", completedConsultations);
        stats.put("totalConsultations", pendingConsultations + inProgressConsultations + completedConsultations);

        return Result.success("查询成功", stats);
    }
}
