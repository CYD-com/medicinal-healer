package com.example.usergenerator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.usergenerator.entity.ConsultationMessage;
import com.example.usergenerator.entity.Doctor;
import com.example.usergenerator.entity.SysUser;
import com.example.usergenerator.mapper.ConsultationMessageMapper;
import com.example.usergenerator.mapper.DoctorMapper;
import com.example.usergenerator.mapper.SysUserMapper;
import com.example.usergenerator.service.ConsultationMessageService;
import com.example.usergenerator.vo.consultation.ConsultationMessageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsultationMessageServiceImpl implements ConsultationMessageService {

    private final ConsultationMessageMapper consultationMessageMapper;
    private final SysUserMapper sysUserMapper;
    private final DoctorMapper doctorMapper;

    @Override
    public ConsultationMessageVO sendMessage(Long consultationId, String senderRole, Long senderId, String content) {
        ConsultationMessage message = new ConsultationMessage();
        message.setConsultationId(consultationId);
        message.setSenderRole(senderRole);
        message.setSenderId(senderId);
        message.setContent(content);
        message.setCreatedAt(LocalDateTime.now());
        consultationMessageMapper.insert(message);
        return convertToVO(message);
    }

    @Override
    public List<ConsultationMessageVO> getMessagesByConsultationId(Long consultationId) {
        LambdaQueryWrapper<ConsultationMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ConsultationMessage::getConsultationId, consultationId);
        wrapper.orderByAsc(ConsultationMessage::getCreatedAt);
        return consultationMessageMapper.selectList(wrapper).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    private ConsultationMessageVO convertToVO(ConsultationMessage message) {
        ConsultationMessageVO vo = new ConsultationMessageVO();
        vo.setId(message.getId());
        vo.setConsultationId(message.getConsultationId());
        vo.setSenderRole(message.getSenderRole());
        vo.setSenderId(message.getSenderId());
        vo.setContent(message.getContent());
        vo.setCreatedAt(message.getCreatedAt());

        String senderName = resolveSenderName(message.getSenderRole(), message.getSenderId());
        vo.setSenderName(senderName);
        return vo;
    }

    private String resolveSenderName(String senderRole, Long senderId) {
        if ("doctor".equals(senderRole)) {
            Doctor doctor = doctorMapper.selectDoctorWithUserById(senderId);
            return doctor != null ? doctor.getName() : "医生";
        } else {
            SysUser user = sysUserMapper.selectById(senderId);
            return user != null ? user.getRealName() : "患者";
        }
    }
}
