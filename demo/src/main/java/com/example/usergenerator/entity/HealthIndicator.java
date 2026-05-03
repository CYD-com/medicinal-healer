package com.example.usergenerator.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("health_indicator")
public class HealthIndicator {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String indicatorType;

    private Integer systolic;

    private Integer diastolic;

    private Integer heartRate;

    private String recordDate;

    private String recordTime;

    private String remark;

    private String source;

    private LocalDateTime createdAt;
}