package com.example.usergenerator.service.impl;

import com.example.usergenerator.dto.healthRecord.AuthorizationCreateDTO;
import com.example.usergenerator.dto.healthRecord.IndicatorCreateDTO;
import com.example.usergenerator.dto.healthRecord.MedicalHistoryUpdateDTO;
import com.example.usergenerator.entity.*;
import com.example.usergenerator.mapper.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.usergenerator.service.HealthRecordService;
import com.example.usergenerator.vo.healthRecord.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HealthRecordServiceImpl implements HealthRecordService {

    private static final java.util.Map<String, String> INDICATOR_TYPE_MAP = java.util.Map.of(
            "bloodPressure", "blood_pressure",
            "bloodSugar", "blood_sugar",
            "heartRate", "heart_rate",
            "weight", "weight",
            "height", "height"
    );

    private String toDbIndicatorType(String type) {
        return INDICATOR_TYPE_MAP.getOrDefault(type, type);
    }

    @Autowired
    private HealthRecordMapper healthRecordMapper;

    @Autowired
    private PastDiseaseMapper pastDiseaseMapper;

    @Autowired
    private FamilyDiseaseMapper familyDiseaseMapper;

    @Autowired
    private AllergyMapper allergyMapper;

    @Autowired
    private SurgicalHistoryMapper surgicalHistoryMapper;

    @Autowired
    private VisitRecordMapper visitRecordMapper;

    @Autowired
    private HealthIndicatorMapper healthIndicatorMapper;

    @Autowired
    private HealthRecordAuthorizationMapper authorizationMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public HealthRecordOverviewVO getOverview(Long userId) {
        HealthRecord record = healthRecordMapper.selectByUserId(userId);
        if (record == null) {
            record = createDefaultRecord(userId);
        }

        HealthRecordOverviewVO vo = new HealthRecordOverviewVO();
        vo.setUserId(String.valueOf(userId));

        HealthRecordOverviewVO.BasicInfoVO basicInfo = new HealthRecordOverviewVO.BasicInfoVO();

        SysUser user = sysUserMapper.selectById(userId);
        if (user != null) {
            basicInfo.setRealName(user.getRealName());
            basicInfo.setGender(user.getGender());
            basicInfo.setAge(user.getAge());
            basicInfo.setPhone(user.getPhone());
        }

        basicInfo.setHeight(record.getHeight());
        basicInfo.setWeight(record.getWeight());
        if (record.getHeight() != null && record.getWeight() != null && record.getHeight() > 0) {
            double heightInMeters = record.getHeight() / 100.0;
            basicInfo.setBmi(Math.round(record.getWeight() / (heightInMeters * heightInMeters) * 10) / 10.0);
        }
        basicInfo.setBloodType(record.getBloodType());
        basicInfo.setMaritalStatus(record.getMaritalStatus());
        vo.setBasicInfo(basicInfo);

        HealthRecordOverviewVO.MedicalHistoryVO medicalHistory = new HealthRecordOverviewVO.MedicalHistoryVO();
        medicalHistory.setPastDiseases(pastDiseaseMapper.selectByUserId(userId).stream()
                .map(PastDisease::getDiseaseName).collect(Collectors.toList()));
        medicalHistory.setFamilyDiseases(familyDiseaseMapper.selectByUserId(userId).stream()
                .map(FamilyDisease::getDiseaseName).collect(Collectors.toList()));
        medicalHistory.setAllergies(allergyMapper.selectByUserId(userId).stream()
                .map(Allergy::getAllergen).collect(Collectors.toList()));
        medicalHistory.setSurgicalHistory(surgicalHistoryMapper.selectByUserId(userId).stream()
                .map(SurgicalHistory::getSurgeryName).collect(Collectors.toList()));
        vo.setMedicalHistory(medicalHistory);

        HealthRecordOverviewVO.StatisticsVO statistics = new HealthRecordOverviewVO.StatisticsVO();
        statistics.setTotalVisits(record.getTotalVisits());
        statistics.setTotalPrescriptions(record.getTotalPrescriptions());
        statistics.setLastVisitDate(record.getLastVisitDate());
        statistics.setHealthScore(record.getHealthScore());
        vo.setStatistics(statistics);

        return vo;
    }

    private HealthRecord createDefaultRecord(Long userId) {
        HealthRecord record = new HealthRecord();
        record.setUserId(userId);
        record.setHealthScore(85);
        record.setTotalVisits(0);
        record.setTotalPrescriptions(0);
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        healthRecordMapper.insert(record);
        return record;
    }

    @Override
    public MedicalHistoryVO getMedicalHistory(Long userId) {
        MedicalHistoryVO vo = new MedicalHistoryVO();

        vo.setPastDiseases(pastDiseaseMapper.selectByUserId(userId).stream().map(d -> {
            MedicalHistoryVO.PastDiseaseVO v = new MedicalHistoryVO.PastDiseaseVO();
            v.setId(d.getId());
            v.setDiseaseName(d.getDiseaseName());
            v.setDiagnosisDate(d.getDiagnosisDate());
            v.setDiagnosisHospital(d.getDiagnosisHospital());
            v.setCurrentStatus(d.getCurrentStatus());
            v.setTreatment(d.getTreatment());
            v.setRemark(d.getRemark());
            return v;
        }).collect(Collectors.toList()));

        vo.setFamilyDiseases(familyDiseaseMapper.selectByUserId(userId).stream().map(d -> {
            MedicalHistoryVO.FamilyDiseaseVO v = new MedicalHistoryVO.FamilyDiseaseVO();
            v.setId(d.getId());
            v.setDiseaseName(d.getDiseaseName());
            v.setRelation(d.getRelation());
            v.setRemark(d.getRemark());
            return v;
        }).collect(Collectors.toList()));

        vo.setAllergies(allergyMapper.selectByUserId(userId).stream().map(a -> {
            MedicalHistoryVO.AllergyVO v = new MedicalHistoryVO.AllergyVO();
            v.setId(a.getId());
            v.setAllergen(a.getAllergen());
            v.setReaction(a.getReaction());
            v.setSeverity(a.getSeverity());
            return v;
        }).collect(Collectors.toList()));

        vo.setSurgicalHistory(surgicalHistoryMapper.selectByUserId(userId).stream().map(s -> {
            MedicalHistoryVO.SurgicalHistoryVO v = new MedicalHistoryVO.SurgicalHistoryVO();
            v.setId(s.getId());
            v.setSurgeryName(s.getSurgeryName());
            v.setSurgeryDate(s.getSurgeryDate());
            v.setHospital(s.getHospital());
            v.setRecovery(s.getRecovery());
            return v;
        }).collect(Collectors.toList()));

        return vo;
    }

    @Override
    @Transactional
    public void updateMedicalHistory(Long userId, MedicalHistoryUpdateDTO dto) {
        if (dto.getPastDiseases() != null) {
            pastDiseaseMapper.deleteByMap(java.util.Map.of("user_id", userId));
            for (MedicalHistoryUpdateDTO.PastDiseaseDTO item : dto.getPastDiseases()) {
                PastDisease disease = new PastDisease();
                disease.setUserId(userId);
                disease.setDiseaseName(item.getDiseaseName());
                disease.setDiagnosisDate(item.getDiagnosisDate());
                disease.setDiagnosisHospital(item.getDiagnosisHospital());
                disease.setCurrentStatus(item.getCurrentStatus());
                disease.setTreatment(item.getTreatment());
                disease.setCreatedAt(LocalDateTime.now());
                disease.setUpdatedAt(LocalDateTime.now());
                pastDiseaseMapper.insert(disease);
            }
        }

        if (dto.getFamilyDiseases() != null) {
            familyDiseaseMapper.deleteByMap(java.util.Map.of("user_id", userId));
            for (MedicalHistoryUpdateDTO.FamilyDiseaseDTO item : dto.getFamilyDiseases()) {
                FamilyDisease familyDisease = new FamilyDisease();
                familyDisease.setUserId(userId);
                familyDisease.setDiseaseName(item.getDiseaseName());
                familyDisease.setRelation(item.getRelation());
                familyDisease.setRemark(item.getRemark());
                familyDisease.setCreatedAt(LocalDateTime.now());
                familyDiseaseMapper.insert(familyDisease);
            }
        }

        if (dto.getAllergies() != null) {
            allergyMapper.deleteByMap(java.util.Map.of("user_id", userId));
            for (MedicalHistoryUpdateDTO.AllergyDTO item : dto.getAllergies()) {
                Allergy allergy = new Allergy();
                allergy.setUserId(userId);
                allergy.setAllergen(item.getAllergen());
                allergy.setReaction(item.getReaction());
                allergy.setSeverity(item.getSeverity());
                allergy.setCreatedAt(LocalDateTime.now());
                allergyMapper.insert(allergy);
            }
        }

        if (dto.getSurgicalHistory() != null) {
            surgicalHistoryMapper.deleteByMap(java.util.Map.of("user_id", userId));
            for (MedicalHistoryUpdateDTO.SurgicalHistoryDTO item : dto.getSurgicalHistory()) {
                SurgicalHistory history = new SurgicalHistory();
                history.setUserId(userId);
                history.setSurgeryName(item.getSurgeryName());
                history.setSurgeryDate(item.getSurgeryDate());
                history.setHospital(item.getHospital());
                history.setRecovery(item.getRecovery());
                history.setCreatedAt(LocalDateTime.now());
                surgicalHistoryMapper.insert(history);
            }
        }
    }

    @Override
    public java.util.Map<String, Object> getVisits(Long userId, String type, String startDate, String endDate, String doctorId) {
        QueryWrapper<VisitRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        if (type != null && !type.isEmpty()) {
            queryWrapper.eq("visit_type", type);
        }
        if (startDate != null && !startDate.isEmpty()) {
            queryWrapper.ge("visit_date", startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            queryWrapper.le("visit_date", endDate);
        }
        if (doctorId != null && !doctorId.isEmpty()) {
            queryWrapper.eq("doctor_id", doctorId);
        }
        queryWrapper.orderByDesc("visit_date");

        List<VisitRecord> records = visitRecordMapper.selectList(queryWrapper);

        List<VisitRecordVO> voList = records.stream().map(record -> {
            VisitRecordVO vo = new VisitRecordVO();
            vo.setVisitId(record.getId());
            vo.setVisitType(record.getVisitType());
            vo.setVisitDate(record.getVisitDate());
            vo.setDepartment(record.getDepartment());
            vo.setChiefComplaint(record.getChiefComplaint());
            vo.setDiagnosis(record.getDiagnosis());
            vo.setTreatment(record.getTreatment());
            vo.setPrescriptionId(record.getPrescriptionId());
            VisitRecordVO.DoctorVO doctor = new VisitRecordVO.DoctorVO();
            doctor.setDoctorId(record.getDoctorId());
            vo.setDoctor(doctor);
            vo.setAttachments(new ArrayList<>());
            return vo;
        }).collect(Collectors.toList());

        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("list", voList);
        result.put("pagination", java.util.Map.of(
                "page", 1,
                "size", voList.size(),
                "total", voList.size(),
                "totalPages", 1,
                "hasNext", false,
                "hasPrev", false
        ));
        return result;
    }

    @Override
    public VisitRecordVO getVisitDetail(Long visitId) {
        VisitRecord record = visitRecordMapper.selectById(visitId);
        if (record == null) {
            return null;
        }

        VisitRecordVO vo = new VisitRecordVO();
        vo.setVisitId(record.getId());
        vo.setVisitType(record.getVisitType());
        vo.setVisitDate(record.getVisitDate());
        vo.setDepartment(record.getDepartment());
        vo.setChiefComplaint(record.getChiefComplaint());
        vo.setDiagnosis(record.getDiagnosis());
        vo.setTreatment(record.getTreatment());
        vo.setPrescriptionId(record.getPrescriptionId());

        VisitRecordVO.DoctorVO doctor = new VisitRecordVO.DoctorVO();
        doctor.setDoctorId(record.getDoctorId());
        vo.setDoctor(doctor);

        vo.setAttachments(new ArrayList<>());
        return vo;
    }

    @Override
    public HealthIndicatorVO getIndicators(Long userId, String type, String startDate, String endDate) {
        String dbType = toDbIndicatorType(type);
        List<HealthIndicator> indicators = healthIndicatorMapper.selectByUserIdAndType(userId, dbType);

        HealthIndicatorVO vo = new HealthIndicatorVO();
        vo.setIndicatorType(type);

        switch (type) {
            case "bloodPressure":
                vo.setUnit("mmHg");
                break;
            case "bloodSugar":
                vo.setUnit("mmol/L");
                break;
            case "weight":
                vo.setUnit("kg");
                break;
            case "height":
                vo.setUnit("cm");
                break;
        }

        HealthIndicatorVO.StatisticsVO statistics = new HealthIndicatorVO.StatisticsVO();
        
        if (!indicators.isEmpty()) {
            HealthIndicator latest = indicators.get(0);
            HealthIndicatorVO.LatestVO latestVO = new HealthIndicatorVO.LatestVO();
            latestVO.setSystolic(latest.getSystolic());
            latestVO.setDiastolic(latest.getDiastolic());
            latestVO.setRecordDate(latest.getRecordDate());
            latestVO.setRecordTime(latest.getRecordTime());
            statistics.setLatest(latestVO);

            int sumSystolic = indicators.stream().mapToInt(i -> i.getSystolic() != null ? i.getSystolic() : 0).sum();
            int sumDiastolic = indicators.stream().mapToInt(i -> i.getDiastolic() != null ? i.getDiastolic() : 0).sum();
            HealthIndicatorVO.AverageVO averageVO = new HealthIndicatorVO.AverageVO();
            averageVO.setSystolic(sumSystolic / indicators.size());
            averageVO.setDiastolic(sumDiastolic / indicators.size());
            statistics.setAverage(averageVO);

            int maxSystolic = indicators.stream().mapToInt(i -> i.getSystolic() != null ? i.getSystolic() : 0).max().orElse(0);
            int maxDiastolic = indicators.stream().mapToInt(i -> i.getDiastolic() != null ? i.getDiastolic() : 0).max().orElse(0);
            HealthIndicatorVO.MaxVO maxVO = new HealthIndicatorVO.MaxVO();
            maxVO.setSystolic(maxSystolic);
            maxVO.setDiastolic(maxDiastolic);
            statistics.setMax(maxVO);

            int minSystolic = indicators.stream().mapToInt(i -> i.getSystolic() != null ? i.getSystolic() : Integer.MAX_VALUE).min().orElse(0);
            int minDiastolic = indicators.stream().mapToInt(i -> i.getDiastolic() != null ? i.getDiastolic() : Integer.MAX_VALUE).min().orElse(0);
            HealthIndicatorVO.MinVO minVO = new HealthIndicatorVO.MinVO();
            minVO.setSystolic(minSystolic);
            minVO.setDiastolic(minDiastolic);
            statistics.setMin(minVO);
        }

        vo.setStatistics(statistics);

        vo.setRecords(indicators.stream().map(i -> {
            HealthIndicatorVO.RecordVO r = new HealthIndicatorVO.RecordVO();
            r.setRecordId(i.getId());
            r.setSystolic(i.getSystolic());
            r.setDiastolic(i.getDiastolic());
            r.setHeartRate(i.getHeartRate());
            r.setRecordDate(i.getRecordDate());
            r.setRecordTime(i.getRecordTime());
            r.setRemark(i.getRemark());
            r.setSource(i.getSource());
            if (!"bloodPressure".equals(type)) {
                r.setValue(i.getSystolic());
            }
            return r;
        }).collect(Collectors.toList()));

        return vo;
    }

    @Override
    public void addIndicator(Long userId, IndicatorCreateDTO dto) {
        HealthIndicator indicator = new HealthIndicator();
        indicator.setUserId(userId);
        indicator.setIndicatorType(toDbIndicatorType(dto.getIndicatorType()));
        indicator.setSystolic(dto.getSystolic());
        indicator.setDiastolic(dto.getDiastolic());
        indicator.setHeartRate(dto.getHeartRate());
        indicator.setRecordDate(dto.getRecordDate());
        indicator.setRecordTime(dto.getRecordTime());
        indicator.setRemark(dto.getRemark());
        indicator.setSource(dto.getSource());
        indicator.setCreatedAt(LocalDateTime.now());
        healthIndicatorMapper.insert(indicator);
    }

    @Override
    public AuthorizationVO getAuthorizations(Long userId) {
        List<HealthRecordAuthorization> authorizations = authorizationMapper.selectByUserId(userId);

        AuthorizationVO vo = new AuthorizationVO();

        vo.setAuthorizedDoctors(authorizations.stream()
                .filter(a -> "doctor".equals(a.getTargetType()))
                .map(a -> {
                    AuthorizationVO.AuthorizedDoctorVO d = new AuthorizationVO.AuthorizedDoctorVO();
                    d.setAuthorizationId(a.getId());
                    d.setDoctorId(a.getTargetId());
                    d.setAuthorizedAt(a.getAuthorizedAt());
                    d.setExpiresAt(a.getExpiresAt());
                    d.setStatus(a.getStatus());
                    try {
                        d.setPermissions(objectMapper.readValue(a.getPermissions(), 
                                objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)));
                    } catch (JsonProcessingException e) {
                        d.setPermissions(new ArrayList<>());
                    }
                    return d;
                }).collect(Collectors.toList()));

        vo.setAuthorizedFamily(authorizations.stream()
                .filter(a -> "family".equals(a.getTargetType()))
                .map(a -> {
                    AuthorizationVO.AuthorizedFamilyVO f = new AuthorizationVO.AuthorizedFamilyVO();
                    f.setAuthorizationId(a.getId());
                    f.setUserId(a.getTargetId());
                    f.setAuthorizedAt(a.getAuthorizedAt());
                    try {
                        f.setPermissions(objectMapper.readValue(a.getPermissions(),
                                objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)));
                    } catch (JsonProcessingException e) {
                        f.setPermissions(new ArrayList<>());
                    }
                    return f;
                }).collect(Collectors.toList()));

        return vo;
    }

    @Override
    public void addAuthorization(Long userId, AuthorizationCreateDTO dto) {
        HealthRecordAuthorization authorization = new HealthRecordAuthorization();
        authorization.setUserId(userId);
        authorization.setTargetType(dto.getTargetType());
        authorization.setTargetId(dto.getTargetId());
        try {
            authorization.setPermissions(objectMapper.writeValueAsString(dto.getPermissions()));
        } catch (JsonProcessingException e) {
            authorization.setPermissions("[]");
        }
        authorization.setExpiresAt(dto.getExpiresAt());
        authorization.setStatus("active");
        authorization.setAuthorizedAt(LocalDateTime.now());
        authorizationMapper.insert(authorization);
    }
}