package com.example.usergenerator.vo.drug;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class InventoryVO {

    private String id;
    private String drugId;
    private String drugName;
    private String specification;
    private Integer stock;
    private Integer safetyStock;
    private String status;
    private LocalDate expiryDate;
    private Integer daysToExpiry;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
