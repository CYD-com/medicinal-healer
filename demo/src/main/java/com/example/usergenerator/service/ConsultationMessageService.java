package com.example.usergenerator.service;

import com.example.usergenerator.entity.ConsultationMessage;
import com.example.usergenerator.vo.consultation.ConsultationMessageVO;

import java.util.List;

public interface ConsultationMessageService {
    ConsultationMessageVO sendMessage(Long consultationId, String senderRole, Long senderId, String content);
    List<ConsultationMessageVO> getMessagesByConsultationId(Long consultationId);
}
