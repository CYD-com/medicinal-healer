package com.example.usergenerator.dto.healthRecord;

import lombok.Data;

import java.util.List;

@Data
public class MedicalHistoryUpdateDTO {

    private List<PastDiseaseDTO> pastDiseases;

    private List<AllergyDTO> allergies;

    @Data
    public static class PastDiseaseDTO {
        private String diseaseName;
        private String diagnosisDate;
        private String diagnosisHospital;
        private String currentStatus;
        private String treatment;
    }

    @Data
    public static class AllergyDTO {
        private String allergen;
        private String reaction;
        private String severity;
    }
}