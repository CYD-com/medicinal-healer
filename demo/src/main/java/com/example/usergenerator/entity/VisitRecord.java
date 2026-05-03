package com.example.usergenerator.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("visit_record")
public class VisitRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String visitType;

    private String visitDate;

    private String department;

    private Long doctorId;

    private String chiefComplaint;

    private String diagnosis;

    private String treatment;

    private Long prescriptionId;

    private LocalDateTime createdAt;
}