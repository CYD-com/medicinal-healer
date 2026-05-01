package com.example.usergenerator.vo.prescription;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PrescriptionVO {

    private Long id;

    private String prescriptionNo;

    private DoctorInfo doctor;

    private String diagnosis;

    private String doctorAdvice;

    private BigDecimal totalAmount;

    private Integer drugCount;

    private String status;

    private String statusText;

    private LocalDateTime validUntil;

    private LocalDateTime createdAt;

    private List<PrescriptionItemVO> drugs;

    @Data
    public static class DoctorInfo {
        private Long id;
        private String name;
        private String title;
        private String avatar;
        private String department;
        private String signature;
    }
}
