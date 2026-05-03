package com.example.usergenerator.dto.healthRecord;

import lombok.Data;

@Data
public class IndicatorQueryDTO {

    private String type;

    private String startDate;

    private String endDate;
}