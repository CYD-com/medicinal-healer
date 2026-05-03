package com.example.usergenerator.vo.healthRecord;

import lombok.Data;

import java.util.List;

@Data
public class MedicalHistoryVO {

    private List<PastDiseaseVO> pastDiseases;

    private List<FamilyDiseaseVO> familyDiseases;

    private List<AllergyVO> allergies;

    private List<SurgicalHistoryVO> surgicalHistory;

    @Data
    public static class PastDiseaseVO {
        private Long id;
        private String diseaseName;
        private String diagnosisDate;
        private String diagnosisHospital;
        private String currentStatus;
        private String treatment;
        private String remark;
    }

    @Data
    public static class FamilyDiseaseVO {
        private Long id;
        private String diseaseName;
        private String relation;
        private String remark;
    }

    @Data
    public static class AllergyVO {
        private Long id;
        private String allergen;
        private String reaction;
        private String severity;
    }

    @Data
    public static class SurgicalHistoryVO {
        private Long id;
        private String surgeryName;
        private String surgeryDate;
        private String hospital;
        private String recovery;
    }
}