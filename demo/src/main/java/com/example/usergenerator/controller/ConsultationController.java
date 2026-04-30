package com.example.usergenerator.controller;

import com.example.usergenerator.annotation.RequirePermission;
import com.example.usergenerator.common.Result;
import com.example.usergenerator.constant.RoleConstants;
import com.example.usergenerator.dto.consultation.ConsultationCreateDTO;
import com.example.usergenerator.service.ConsultationService;
import com.example.usergenerator.util.PermissionUtil;
import com.example.usergenerator.vo.consultation.ConsultationVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/consultation")
@RequiredArgsConstructor
@Validated
public class ConsultationController {

    private final ConsultationService consultationService;
    private final PermissionUtil permissionUtil;

    @PostMapping("/create")
    @RequirePermission({RoleConstants.USER, RoleConstants.ADMIN})
    public Result<ConsultationVO> createConsultation(@Valid @RequestBody ConsultationCreateDTO dto) {
        Long userId = permissionUtil.getCurrentUserId();
        ConsultationVO consultation = consultationService.createConsultation(dto, userId);
        return Result.success("问诊提交成功", consultation);
    }

    @GetMapping("/list")
    @RequirePermission({RoleConstants.USER, RoleConstants.ADMIN})
    public Result<List<ConsultationVO>> getConsultations(
            @RequestParam(required = false) String status) {
        Long userId = permissionUtil.getCurrentUserId();
        List<ConsultationVO> consultations = consultationService.getConsultationsByUserId(userId, status);
        return Result.success("查询成功", consultations);
    }

    @GetMapping("/{id}")
    @RequirePermission({RoleConstants.USER, RoleConstants.ADMIN})
    public Result<ConsultationVO> getConsultationById(@PathVariable Long id) {
        ConsultationVO consultation = consultationService.getConsultationById(id);
        if (consultation == null) {
            return Result.error(404, "问诊记录不存在");
        }
        return Result.success("查询成功", consultation);
    }

    // 取消问诊
    @PutMapping("/{id}/cancel")
    @RequirePermission({RoleConstants.USER, RoleConstants.ADMIN})
    public Result<ConsultationVO> cancelConsultation(@PathVariable Long id) {
        Long userId = permissionUtil.getCurrentUserId();
        ConsultationVO consultation = consultationService.cancelConsultation(id, userId);
        if (consultation == null) {
            return Result.error(400, "取消失败，问诊记录不存在或已处理");
        }
        return Result.success("取消成功", consultation);
    }
}