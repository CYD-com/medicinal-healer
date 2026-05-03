package com.example.usergenerator.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("surgical_history")
public class SurgicalHistory {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String surgeryName;

    private String surgeryDate;

    private String hospital;

    private String recovery;

    private LocalDateTime createdAt;
}