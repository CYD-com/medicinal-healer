package com.example.usergenerator.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("consultation_message")
public class ConsultationMessage {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("consultation_id")
    private Long consultationId;

    @TableField("sender_role")
    private String senderRole;

    @TableField("sender_id")
    private Long senderId;

    @TableField("content")
    private String content;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
