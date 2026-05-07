package com.example.usergenerator.controller;

import com.example.usergenerator.annotation.RequirePermission;
import com.example.usergenerator.annotation.LogOperation;
import com.example.usergenerator.common.Result;
import com.example.usergenerator.constant.RoleConstants;
import com.example.usergenerator.dto.consultation.ConsultationCreateDTO;
import com.example.usergenerator.entity.Doctor;
import com.example.usergenerator.entity.Consultation;
import com.example.usergenerator.mapper.DoctorMapper;
import com.example.usergenerator.service.ConsultationService;
import com.example.usergenerator.util.PermissionUtil;
import com.example.usergenerator.vo.consultation.ConsultationVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/consultation")
@RequiredArgsConstructor
@Validated
public class ConsultationController {

    private final ConsultationService consultationService;
    private final PermissionUtil permissionUtil;
    private final DoctorMapper doctorMapper;

    @PostMapping("/create")
    @RequirePermission({RoleConstants.USER, RoleConstants.ADMIN})
    @LogOperation(operationType = "create", targetType = "consultation", description = "创建问诊")
    public Result<ConsultationVO> createConsultation(@Valid @RequestBody ConsultationCreateDTO dto) {
        Long userId = permissionUtil.getCurrentUserId();
        ConsultationVO consultation = consultationService.createConsultation(dto, userId);
        return Result.success("问诊提交成功", consultation);
    }

    @GetMapping("/list")
    @RequirePermission({RoleConstants.USER, RoleConstants.ADMIN})
    public Result<IPage<ConsultationVO>> getConsultations(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = permissionUtil.getCurrentUserId();
        Page<Consultation> pageParam = new Page<>(page, size);
        IPage<ConsultationVO> consultations = consultationService.getConsultationsByUserIdPage(userId, status, pageParam);
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

    // 医生端 - 获取待处理问诊列表
    @GetMapping("/doctor/list")
    @RequirePermission({RoleConstants.DOCTOR, RoleConstants.ADMIN})
    public Result<IPage<ConsultationVO>> getDoctorConsultations(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = permissionUtil.getCurrentUserId();
        Doctor doctor = doctorMapper.selectByUserId(userId);
        if (doctor == null) {
            return Result.error(404, "医生信息不存在");
        }
        Page<Consultation> pageParam = new Page<>(page, size);
        IPage<ConsultationVO> consultations = consultationService.getConsultationsByDoctorIdPage(doctor.getId(), status, pageParam);
        return Result.success("查询成功", consultations);
    }

    // 医生端 - 回复问诊
    @PutMapping("/{id}/reply")
    @RequirePermission({RoleConstants.DOCTOR, RoleConstants.ADMIN})
    @LogOperation(operationType = "reply", targetType = "consultation", description = "回复问诊")
    public Result<ConsultationVO> replyConsultation(
            @PathVariable Long id,
            @RequestBody java.util.Map<String, String> body) {
        Long userId = permissionUtil.getCurrentUserId();
        Doctor doctor = doctorMapper.selectByUserId(userId);
        if (doctor == null) {
            return Result.error(404, "医生信息不存在");
        }
        String doctorReply = body.get("doctorReply");
        String diagnosis = body.get("diagnosis");
        String status = body.getOrDefault("status", "in_progress");
        ConsultationVO consultation = consultationService.updateConsultationByDoctor(id, doctor.getId(), doctorReply, diagnosis, status);
        return Result.success("回复成功", consultation);
    }

    // 医生端 - 完成问诊
    @PutMapping("/{id}/complete")
    @RequirePermission({RoleConstants.DOCTOR, RoleConstants.ADMIN})
    public Result<ConsultationVO> completeConsultation(
            @PathVariable Long id,
            @RequestBody java.util.Map<String, String> body) {
        Long userId = permissionUtil.getCurrentUserId();
        Doctor doctor = doctorMapper.selectByUserId(userId);
        if (doctor == null) {
            return Result.error(404, "医生信息不存在");
        }
        String diagnosis = body.get("diagnosis");
        ConsultationVO consultation = consultationService.updateConsultationByDoctor(id, doctor.getId(), null, diagnosis, "completed");
        return Result.success("问诊已完成", consultation);
    }
}