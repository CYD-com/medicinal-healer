package com.example.usergenerator.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("past_disease")
public class PastDisease {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String diseaseName;

    private String diagnosisDate;

    private String diagnosisHospital;

    private String currentStatus;

    private String treatment;

    private String remark;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}