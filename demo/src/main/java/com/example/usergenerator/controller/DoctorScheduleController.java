package com.example.usergenerator.controller;

import com.example.usergenerator.common.Result;
import com.example.usergenerator.constant.RoleConstants;
import com.example.usergenerator.dto.schedule.DoctorScheduleDTO;
import com.example.usergenerator.dto.schedule.ScheduleQueryDTO;
import com.example.usergenerator.entity.DoctorSchedule;
import com.example.usergenerator.service.DoctorScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class DoctorScheduleController {

    private final DoctorScheduleService doctorScheduleService;

    @PostMapping("/create")
    public ResponseEntity<?> createSchedule(@Valid @RequestBody DoctorScheduleDTO dto) {
        DoctorSchedule schedule = doctorScheduleService.createSchedule(dto);
        return ResponseEntity.ok(Result.success("排班创建成功", schedule));
    }

    @GetMapping("/list")
    public ResponseEntity<?> getScheduleList(ScheduleQueryDTO query) {
        Map<String, Object> result = doctorScheduleService.getScheduleList(query);
        return ResponseEntity.ok(Result.success(result));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<?> getSchedulesByDoctor(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<DoctorSchedule> schedules = doctorScheduleService.getSchedulesByDoctor(doctorId, startDate, endDate);
        return ResponseEntity.ok(Result.success(schedules));
    }

    @GetMapping("/department/{departmentId}/date/{workDate}")
    public ResponseEntity<?> getSchedulesByDepartment(
            @PathVariable Long departmentId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate workDate) {
        List<DoctorSchedule> schedules = doctorScheduleService.getSchedulesByDepartment(departmentId, workDate);
        return ResponseEntity.ok(Result.success(schedules));
    }

    @GetMapping("/doctor/{doctorId}/date/{workDate}/slots")
    public ResponseEntity<?> getAvailableTimeSlots(
            @PathVariable Long doctorId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate workDate) {
        List<Map<String, Object>> slots = doctorScheduleService.getAvailableTimeSlots(doctorId, workDate);
        return ResponseEntity.ok(Result.success(slots));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = doctorScheduleService.updateStatus(id, status);
        return ResponseEntity.ok(Result.success(success ? "状态更新成功" : "状态更新失败"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Long id) {
        boolean success = doctorScheduleService.deleteSchedule(id);
        return ResponseEntity.ok(Result.success("排班删除成功"));
    }
}
