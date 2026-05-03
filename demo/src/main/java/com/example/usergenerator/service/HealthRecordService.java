package com.example.usergenerator.service;

import com.example.usergenerator.dto.healthRecord.AuthorizationCreateDTO;
import com.example.usergenerator.dto.healthRecord.IndicatorCreateDTO;
import com.example.usergenerator.dto.healthRecord.MedicalHistoryUpdateDTO;
import com.example.usergenerator.vo.healthRecord.*;

public interface HealthRecordService {

    HealthRecordOverviewVO getOverview(Long userId);

    MedicalHistoryVO getMedicalHistory(Long userId);

    void updateMedicalHistory(Long userId, MedicalHistoryUpdateDTO dto);

    java.util.Map<String, Object> getVisits(Long userId, String type, String startDate, String endDate);

    VisitRecordVO getVisitDetail(Long visitId);

    HealthIndicatorVO getIndicators(Long userId, String type, String startDate, String endDate);

    void addIndicator(Long userId, IndicatorCreateDTO dto);

    AuthorizationVO getAuthorizations(Long userId);

    void addAuthorization(Long userId, AuthorizationCreateDTO dto);
}