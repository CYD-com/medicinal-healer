package com.example.usergenerator.controller;

import com.example.usergenerator.annotation.RequirePermission;
import com.example.usergenerator.common.Result;
import com.example.usergenerator.constant.RoleConstants;
import com.example.usergenerator.dto.prescription.PrescriptionCreateDTO;
import com.example.usergenerator.service.PrescriptionService;
import com.example.usergenerator.util.PermissionUtil;
import com.example.usergenerator.vo.prescription.PrescriptionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/prescription")
@RequiredArgsConstructor
@Validated
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final PermissionUtil permissionUtil;

    @PostMapping("/create")
    @RequirePermission({RoleConstants.USER, RoleConstants.ADMIN})
    public Result<PrescriptionVO> createPrescription(@Valid @RequestBody PrescriptionCreateDTO dto) {
        Long userId = permissionUtil.getCurrentUserId();
        PrescriptionVO prescription = prescriptionService.createPrescription(dto, userId);
        return Result.success("创建处方成功", prescription);
    }

    @GetMapping("/list")
    @RequirePermission({RoleConstants.USER, RoleConstants.ADMIN})
    public Result<List<PrescriptionVO>> getPrescriptions(
            @RequestParam(required = false) String status) {
        Long userId = permissionUtil.getCurrentUserId();
        List<PrescriptionVO> prescriptions = prescriptionService.getPrescriptionsByUserId(userId, status);
        return Result.success("查询成功", prescriptions);
    }

    @GetMapping("/{id}")
    @RequirePermission({RoleConstants.USER, RoleConstants.ADMIN})
    public Result<PrescriptionVO> getPrescriptionById(@PathVariable Long id) {
        PrescriptionVO prescription = prescriptionService.getPrescriptionById(id);
        return Result.success("查询成功", prescription);
    }

    @PutMapping("/{id}/status")
    @RequirePermission(RoleConstants.ADMIN)
    public Result<PrescriptionVO> updatePrescriptionStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        PrescriptionVO prescription = prescriptionService.updatePrescriptionStatus(id, status);
        return Result.success("状态更新成功", prescription);
    }
}
