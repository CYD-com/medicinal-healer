package com.example.usergenerator.controller;

import com.example.usergenerator.annotation.RequirePermission;
import com.example.usergenerator.common.Result;
import com.example.usergenerator.constant.RoleConstants;
import com.example.usergenerator.entity.Consultation;
import com.example.usergenerator.entity.HealthIndicator;
import com.example.usergenerator.mapper.HealthIndicatorMapper;
import com.example.usergenerator.service.AppointmentService;
import com.example.usergenerator.service.ConsultationService;
import com.example.usergenerator.service.DrugService;
import com.example.usergenerator.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final SysUserService userService;
    private final AppointmentService appointmentService;
    private final ConsultationService consultationService;
    private final DrugService drugService;
    private final HealthIndicatorMapper healthIndicatorMapper;

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

    @GetMapping("/health-trend")
    @RequirePermission(RoleConstants.USER)
    public Result<Map<String, Object>> getHealthTrend(
            @RequestParam(required = false) Long userId,
            @RequestParam String indicatorType,
            @RequestParam(defaultValue = "30") Integer days) {
        LambdaQueryWrapper<HealthIndicator> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(HealthIndicator::getUserId, userId);
        }
        wrapper.eq(HealthIndicator::getIndicatorType, indicatorType);
        wrapper.orderByAsc(HealthIndicator::getRecordDate, HealthIndicator::getRecordTime);
        wrapper.last("LIMIT " + days);

        List<HealthIndicator> indicators = healthIndicatorMapper.selectList(wrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("indicatorType", indicatorType);
        result.put("count", indicators.size());

        List<Map<String, Object>> records = indicators.stream().map(ind -> {
            Map<String, Object> record = new HashMap<>();
            record.put("date", ind.getRecordDate());
            record.put("time", ind.getRecordTime());
            record.put("systolic", ind.getSystolic());
            record.put("diastolic", ind.getDiastolic());
            record.put("heartRate", ind.getHeartRate());
            record.put("remark", ind.getRemark());
            record.put("source", ind.getSource());
            return record;
        }).collect(Collectors.toList());
        result.put("records", records);

        if ("blood_pressure".equals(indicatorType) && !indicators.isEmpty()) {
            OptionalDouble avgSystolic = indicators.stream()
                    .filter(i -> i.getSystolic() != null)
                    .mapToInt(HealthIndicator::getSystolic).average();
            OptionalDouble avgDiastolic = indicators.stream()
                    .filter(i -> i.getDiastolic() != null)
                    .mapToInt(HealthIndicator::getDiastolic).average();
            Map<String, Object> avg = new HashMap<>();
            avgSystolic.ifPresent(v -> avg.put("systolic", Math.round(v * 10.0) / 10.0));
            avgDiastolic.ifPresent(v -> avg.put("diastolic", Math.round(v * 10.0) / 10.0));
            result.put("average", avg);
        }

        if ("heart_rate".equals(indicatorType) && !indicators.isEmpty()) {
            OptionalDouble avgHeartRate = indicators.stream()
                    .filter(i -> i.getHeartRate() != null)
                    .mapToInt(HealthIndicator::getHeartRate).average();
            Map<String, Object> avg = new HashMap<>();
            avgHeartRate.ifPresent(v -> avg.put("heartRate", Math.round(v * 10.0) / 10.0));
            result.put("average", avg);
        }

        return Result.success("查询成功", result);
    }
}
