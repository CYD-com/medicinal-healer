package com.example.usergenerator.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_prescription_item")
public class PrescriptionItem {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("prescription_id")
    private Long prescriptionId;

    @TableField("drug_id")
    private String drugId;

    @TableField("drug_name")
    private String drugName;

    @TableField("specification")
    private String specification;

    @TableField("dosage")
    private String dosage;

    @TableField("quantity")
    private Integer quantity;

    @TableField("unit")
    private String unit;

    @TableField("unit_price")
    private BigDecimal unitPrice;

    @TableField("amount")
    private BigDecimal amount;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
