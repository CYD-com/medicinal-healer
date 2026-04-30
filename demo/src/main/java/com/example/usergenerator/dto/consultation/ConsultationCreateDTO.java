package com.example.usergenerator.dto.consultation;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ConsultationCreateDTO {

    @NotNull(message = "医生ID不能为空")
    private Long doctorId;

    @NotBlank(message = "问诊类型不能为空")
    private String consultationType;

    @NotBlank(message = "主诉不能为空")
    private String symptom;

    @NotBlank(message = "详细描述不能为空")
    private String description;

    private String images;

    private String medicalHistory;

    private String currentMedication;
}