package com.example.usergenerator.controller;

import com.example.usergenerator.annotation.RequirePermission;
import com.example.usergenerator.common.Result;
import com.example.usergenerator.constant.RoleConstants;
import com.example.usergenerator.dto.healthRecord.AuthorizationCreateDTO;
import com.example.usergenerator.dto.healthRecord.IndicatorCreateDTO;
import com.example.usergenerator.dto.healthRecord.MedicalHistoryUpdateDTO;
import com.example.usergenerator.service.HealthRecordService;
import com.example.usergenerator.util.PermissionUtil;
import com.example.usergenerator.vo.healthRecord.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/health-records")
@RequiredArgsConstructor
public class HealthRecordController {

    private final HealthRecordService healthRecordService;
    private final PermissionUtil permissionUtil;

    @GetMapping("/overview")
    @RequirePermission(RoleConstants.USER)
    public Result<HealthRecordOverviewVO> getOverview() {
        Long userId = permissionUtil.getCurrentUserId();
        return Result.success(healthRecordService.getOverview(userId));
    }

    @GetMapping("/medical-history")
    @RequirePermission(RoleConstants.USER)
    public Result<MedicalHistoryVO> getMedicalHistory() {
        Long userId = permissionUtil.getCurrentUserId();
        return Result.success(healthRecordService.getMedicalHistory(userId));
    }

    @PutMapping("/medical-history")
    @RequirePermission(RoleConstants.USER)
    public Result<Void> updateMedicalHistory(@RequestBody MedicalHistoryUpdateDTO dto) {
        Long userId = permissionUtil.getCurrentUserId();
        healthRecordService.updateMedicalHistory(userId, dto);
        return Result.success("更新成功");
    }

    @GetMapping("/visits")
    @RequirePermission(RoleConstants.USER)
    public Result<?> getVisits(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String doctorId) {
        Long userId = permissionUtil.getCurrentUserId();
        return Result.success(healthRecordService.getVisits(userId, type, startDate, endDate, doctorId));
    }

    @GetMapping("/visits/{visitId}")
    @RequirePermission(RoleConstants.USER)
    public Result<VisitRecordVO> getVisitDetail(@PathVariable Long visitId) {
        return Result.success(healthRecordService.getVisitDetail(visitId));
    }

    @GetMapping("/indicators")
    @RequirePermission(RoleConstants.USER)
    public Result<HealthIndicatorVO> getIndicators(
            @RequestParam String type,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Long userId = permissionUtil.getCurrentUserId();
        return Result.success(healthRecordService.getIndicators(userId, type, startDate, endDate));
    }

    @PostMapping("/indicators")
    @RequirePermission(RoleConstants.USER)
    public Result<Void> addIndicator(@RequestBody IndicatorCreateDTO dto) {
        Long userId = permissionUtil.getCurrentUserId();
        healthRecordService.addIndicator(userId, dto);
        return Result.success("添加成功");
    }

    @GetMapping("/authorizations")
    @RequirePermission(RoleConstants.USER)
    public Result<AuthorizationVO> getAuthorizations() {
        Long userId = permissionUtil.getCurrentUserId();
        return Result.success(healthRecordService.getAuthorizations(userId));
    }

    @PostMapping("/authorizations")
    @RequirePermission(RoleConstants.USER)
    public Result<Void> addAuthorization(@RequestBody AuthorizationCreateDTO dto) {
        Long userId = permissionUtil.getCurrentUserId();
        healthRecordService.addAuthorization(userId, dto);
        return Result.success("授权成功");
    }
}
