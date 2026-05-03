package com.example.usergenerator.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("health_record")
public class HealthRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Integer height;

    private Integer weight;

    private String bloodType;

    private String maritalStatus;

    private Integer healthScore;

    private Integer totalVisits;

    private Integer totalPrescriptions;

    private String lastVisitDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}