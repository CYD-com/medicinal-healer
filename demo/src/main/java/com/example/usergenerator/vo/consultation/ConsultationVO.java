package com.example.usergenerator.vo.consultation;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConsultationVO {

    private Long id;

    private String consultationNo;

    private DoctorVO doctor;

    private String consultationType;

    private String symptom;

    private String description;

    private String images;

    private String medicalHistory;

    private String currentMedication;

    private String status;

    private String statusText;

    private LocalDateTime createdAt;

    @Data
    public static class DoctorVO {
        private Long id;
        private String name;
        private String title;
        private String avatar;
    }
}