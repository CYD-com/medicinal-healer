package com.example.usergenerator.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("drugs")
public class Drug {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String drugName;
    private String genericName;
    private String specification;
    private String manufacturer;
    private String category;
    private String type;
    private Double price;
    private Integer stock;
    private String unit;
    private String englishName;
    private String approvalNo;
    private String form;
    private String shelfLife;
    private String storage;
    private String indications;
    private String contraindications;
    private String adverseReactions;
    private String precautions;
    private String drugInteractions;
    private String dosage;
    private String image;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
