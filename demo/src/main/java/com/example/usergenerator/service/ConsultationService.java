package com.example.usergenerator.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.usergenerator.dto.consultation.ConsultationCreateDTO;
import com.example.usergenerator.entity.Consultation;
import com.example.usergenerator.vo.consultation.ConsultationVO;

import java.util.List;

public interface ConsultationService extends IService<Consultation> {
    ConsultationVO createConsultation(ConsultationCreateDTO dto, Long userId);
    List<ConsultationVO> getConsultationsByUserId(Long userId, String status);
    ConsultationVO getConsultationById(Long id);
    ConsultationVO cancelConsultation(Long id, Long userId);

    List<ConsultationVO> getConsultationsByDoctorId(Long doctorId, String status);
    ConsultationVO updateConsultationByDoctor(Long id, Long doctorId, String doctorReply, String diagnosis, String status);
}