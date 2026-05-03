package com.example.usergenerator.dto.healthRecord;

import lombok.Data;

@Data
public class IndicatorCreateDTO {

    private String indicatorType;

    private Integer systolic;

    private Integer diastolic;

    private Integer heartRate;

    private String recordDate;

    private String recordTime;

    private String remark;

    private String source;
}