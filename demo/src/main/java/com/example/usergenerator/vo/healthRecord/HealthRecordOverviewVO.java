package com.example.usergenerator.vo.healthRecord;

import lombok.Data;

import java.util.List;

@Data
public class HealthRecordOverviewVO {

    private String userId;

    private BasicInfoVO basicInfo;

    private MedicalHistoryVO medicalHistory;

    private StatisticsVO statistics;

    @Data
    public static class BasicInfoVO {
        private String realName;
        private String gender;
        private Integer age;
        private String phone;
        private Integer height;
        private Integer weight;
        private Double bmi;
        private String bloodType;
        private String maritalStatus;
    }

    @Data
    public static class MedicalHistoryVO {
        private List<String> pastDiseases;
        private List<String> familyDiseases;
        private List<String> allergies;
        private List<String> surgicalHistory;
    }

    @Data
    public static class StatisticsVO {
        private Integer totalVisits;
        private Integer totalPrescriptions;
        private String lastVisitDate;
        private Integer healthScore;
    }
}