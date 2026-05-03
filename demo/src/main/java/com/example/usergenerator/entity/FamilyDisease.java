package com.example.usergenerator.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("family_disease")
public class FamilyDisease {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String diseaseName;

    private String relation;

    private String remark;

    private LocalDateTime createdAt;
}