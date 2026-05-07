package com.example.usergenerator.controller;

import com.example.usergenerator.annotation.RequirePermission;
import com.example.usergenerator.annotation.LogOperation;
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

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.usergenerator.entity.Prescription;

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
    @RequirePermission({RoleConstants.USER, RoleConstants.DOCTOR, RoleConstants.ADMIN})
    @LogOperation(operationType = "create", targetType = "prescription", description = "创建处方")
    public Result<PrescriptionVO> createPrescription(@Valid @RequestBody PrescriptionCreateDTO dto) {
        Long userId = permissionUtil.getCurrentUserId();
        PrescriptionVO prescription = prescriptionService.createPrescription(dto, userId);
        return Result.success("创建处方成功", prescription);
    }

    @GetMapping("/list")
    @RequirePermission({RoleConstants.USER, RoleConstants.DOCTOR, RoleConstants.ADMIN})
    public Result<IPage<PrescriptionVO>> getPrescriptions(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String prescriptionNo,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = permissionUtil.getCurrentUserId();
        String role = permissionUtil.getCurrentUserRole();
        Page<Prescription> pageParam = new Page<>(page, size);
        IPage<PrescriptionVO> result;
        if (RoleConstants.DOCTOR.equals(role)) {
            result = prescriptionService.getPrescriptionsByDoctorUserIdPage(userId, status, prescriptionNo, pageParam);
        } else {
            result = prescriptionService.getPrescriptionsByUserIdPage(userId, status, pageParam);
        }
        return Result.success("查询成功", result);
    }

    @DeleteMapping("/{id}")
    @RequirePermission({RoleConstants.DOCTOR, RoleConstants.ADMIN})
    @LogOperation(operationType = "delete", targetType = "prescription", description = "删除处方")
    public Result<Void> deletePrescription(@PathVariable Long id) {
        boolean success = prescriptionService.deletePrescription(id);
        if (!success) {
            return Result.error(404, "处方不存在");
        }
        return Result.success("删除成功");
    }

    @GetMapping("/{id}")
    @RequirePermission({RoleConstants.USER, RoleConstants.DOCTOR, RoleConstants.ADMIN})
    public Result<PrescriptionVO> getPrescriptionById(@PathVariable Long id) {
        PrescriptionVO prescription = prescriptionService.getPrescriptionById(id);
        return Result.success("查询成功", prescription);
    }

    @PutMapping("/{id}/status")
    @RequirePermission(RoleConstants.ADMIN)
    @LogOperation(operationType = "update_status", targetType = "prescription", description = "更新处方状态")
    public Result<PrescriptionVO> updatePrescriptionStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        PrescriptionVO prescription = prescriptionService.updatePrescriptionStatus(id, status);
        return Result.success("状态更新成功", prescription);
    }
}
