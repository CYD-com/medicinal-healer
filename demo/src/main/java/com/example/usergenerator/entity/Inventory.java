package com.example.usergenerator.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("inventory")
public class Inventory {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String drugId;
    private Integer stock;
    private Integer safetyStock;
    private String status;
    private LocalDate expiryDate;
    private Integer daysToExpiry;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
