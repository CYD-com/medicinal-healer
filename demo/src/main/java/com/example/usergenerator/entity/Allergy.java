package com.example.usergenerator.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("allergy")
public class Allergy {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String allergen;

    private String reaction;

    private String severity;

    private LocalDateTime createdAt;
}