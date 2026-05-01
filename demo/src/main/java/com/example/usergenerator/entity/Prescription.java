package com.example.usergenerator.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_prescription")
public class Prescription {

    @TableId(value = "prescription_id", type = IdType.AUTO)
    private Long id;

    @TableField("prescription_no")
    private String prescriptionNo;

    @TableField("user_id")
    private Long userId;

    @TableField("doctor_id")
    private Long doctorId;

    @TableField("diagnosis")
    private String diagnosis;

    @TableField("doctor_advice")
    private String doctorAdvice;

    @TableField("total_amount")
    private BigDecimal totalAmount;

    @TableField("drug_count")
    private Integer drugCount;

    @TableField("status")
    private String status;

    @TableField("valid_until")
    private LocalDateTime validUntil;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    @TableField("deleted")
    private Integer deleted;
}
