package com.example.usergenerator.vo.prescription;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PrescriptionItemVO {

    private Long id;

    private String drugId;

    private String drugName;

    private String specification;

    private String dosage;

    private Integer quantity;

    private String unit;

    private BigDecimal unitPrice;

    private BigDecimal amount;
}
