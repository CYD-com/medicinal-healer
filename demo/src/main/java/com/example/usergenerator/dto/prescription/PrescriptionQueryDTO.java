package com.example.usergenerator.dto.prescription;

import lombok.Data;

@Data
public class PrescriptionQueryDTO {

    private String prescriptionNo;

    private String status;

    private Integer page = 1;

    private Integer size = 10;
}
