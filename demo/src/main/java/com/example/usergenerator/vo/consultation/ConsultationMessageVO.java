package com.example.usergenerator.vo.consultation;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConsultationMessageVO {
    private Long id;
    private Long consultationId;
    private String senderRole;
    private Long senderId;
    private String senderName;
    private String content;
    private LocalDateTime createdAt;
}
