package com.example.usergenerator.dto.healthRecord;

import lombok.Data;

import java.util.List;

@Data
public class MedicalHistoryUpdateDTO {

    private List<PastDiseaseDTO> pastDiseases;

    private List<FamilyDiseaseDTO> familyDiseases;

    private List<AllergyDTO> allergies;

    private List<SurgicalHistoryDTO> surgicalHistory;

    @Data
    public static class PastDiseaseDTO {
        private String diseaseName;
        private String diagnosisDate;
        private String diagnosisHospital;
        private String currentStatus;
        private String treatment;
    }

    @Data
    public static class FamilyDiseaseDTO {
        private String diseaseName;
        private String relation;
        private String remark;
    }

    @Data
    public static class AllergyDTO {
        private String allergen;
        private String reaction;
        private String severity;
    }

    @Data
    public static class SurgicalHistoryDTO {
        private String surgeryName;
        private String surgeryDate;
        private String hospital;
        private String recovery;
    }
}