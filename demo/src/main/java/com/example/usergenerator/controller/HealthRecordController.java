package com.example.usergenerator.controller;

import com.example.usergenerator.dto.healthRecord.AuthorizationCreateDTO;
import com.example.usergenerator.dto.healthRecord.IndicatorCreateDTO;
import com.example.usergenerator.dto.healthRecord.MedicalHistoryUpdateDTO;
import com.example.usergenerator.service.HealthRecordService;
import com.example.usergenerator.vo.healthRecord.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/health-records")
public class HealthRecordController {

    @Autowired
    private HealthRecordService healthRecordService;

    @GetMapping("/overview")
    public ResponseEntity<HealthRecordOverviewVO> getOverview() {
        Long userId = 1L;
        return ResponseEntity.ok(healthRecordService.getOverview(userId));
    }

    @GetMapping("/medical-history")
    public ResponseEntity<MedicalHistoryVO> getMedicalHistory() {
        Long userId = 1L;
        return ResponseEntity.ok(healthRecordService.getMedicalHistory(userId));
    }

    @PutMapping("/medical-history")
    public ResponseEntity<Void> updateMedicalHistory(@RequestBody MedicalHistoryUpdateDTO dto) {
        Long userId = 1L;
        healthRecordService.updateMedicalHistory(userId, dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/visits")
    public ResponseEntity<?> getVisits(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String doctorId) {
        Long userId = 1L;
        return ResponseEntity.ok(healthRecordService.getVisits(userId, type, startDate, endDate));
    }

    @GetMapping("/visits/{visitId}")
    public ResponseEntity<VisitRecordVO> getVisitDetail(@PathVariable Long visitId) {
        return ResponseEntity.ok(healthRecordService.getVisitDetail(visitId));
    }

    @GetMapping("/indicators")
    public ResponseEntity<HealthIndicatorVO> getIndicators(
            @RequestParam String type,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Long userId = 1L;
        return ResponseEntity.ok(healthRecordService.getIndicators(userId, type, startDate, endDate));
    }

    @PostMapping("/indicators")
    public ResponseEntity<Void> addIndicator(@RequestBody IndicatorCreateDTO dto) {
        Long userId = 1L;
        healthRecordService.addIndicator(userId, dto);
        return ResponseEntity.status(201).build();
    }

    @GetMapping("/authorizations")
    public ResponseEntity<AuthorizationVO> getAuthorizations() {
        Long userId = 1L;
        return ResponseEntity.ok(healthRecordService.getAuthorizations(userId));
    }

    @PostMapping("/authorizations")
    public ResponseEntity<Void> addAuthorization(@RequestBody AuthorizationCreateDTO dto) {
        Long userId = 1L;
        healthRecordService.addAuthorization(userId, dto);
        return ResponseEntity.status(201).build();
    }
}