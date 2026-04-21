package com.example.usergenerator.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("drug_categories")
public class DrugCategory {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
}
