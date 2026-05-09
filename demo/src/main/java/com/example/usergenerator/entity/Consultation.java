package com.example.usergenerator.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("consultation")
public class Consultation {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("consultation_no")
    private String consultationNo;

    @TableField("user_id")
    private Long userId;

    @TableField("doctor_id")
    private Long doctorId;

    @TableField("consultation_type")
    private String consultationType;

    @TableField("symptom")
    private String symptom;

    @TableField("description")
    private String description;

    @TableField("images")
    private String images;

    @TableField("medical_history")
    private String medicalHistory;

    @TableField("current_medication")
    private String currentMedication;

    @TableField("doctor_reply")
    private String doctorReply;

    @TableField("patient_message")
    private String patientMessage;

    @TableField("diagnosis")
    private String diagnosis;

    @TableField("status")
    private String status;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    @TableField("deleted")
    private Integer deleted;
}