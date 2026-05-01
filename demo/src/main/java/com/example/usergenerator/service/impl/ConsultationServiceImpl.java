package com.example.usergenerator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.usergenerator.dto.consultation.ConsultationCreateDTO;
import com.example.usergenerator.entity.Consultation;
import com.example.usergenerator.entity.Doctor;
import com.example.usergenerator.mapper.ConsultationMapper;
import com.example.usergenerator.mapper.DoctorMapper;
import com.example.usergenerator.service.ConsultationService;
import com.example.usergenerator.vo.consultation.ConsultationVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsultationServiceImpl extends ServiceImpl<ConsultationMapper, Consultation> implements ConsultationService {

    private final DoctorMapper doctorMapper;

    private static final Map<String, String> STATUS_MAP = Map.of(
            "pending", "待回复",
            "in_progress", "进行中",
            "completed", "已完成",
            "closed", "已关闭"
    );

    @Override
    @Transactional
    public ConsultationVO createConsultation(ConsultationCreateDTO dto, Long userId) {
        Consultation consultation = new Consultation();
        consultation.setConsultationNo(generateConsultationNo());
        consultation.setUserId(userId);
        consultation.setDoctorId(dto.getDoctorId());
        consultation.setConsultationType(dto.getConsultationType());
        consultation.setSymptom(dto.getSymptom());
        consultation.setDescription(dto.getDescription());
        consultation.setImages(dto.getImages());
        consultation.setMedicalHistory(dto.getMedicalHistory());
        consultation.setCurrentMedication(dto.getCurrentMedication());
        consultation.setStatus("pending");
        consultation.setCreatedAt(LocalDateTime.now());
        consultation.setUpdatedAt(LocalDateTime.now());
        consultation.setDeleted(0);

        baseMapper.insert(consultation);

        return convertToVO(consultation);
    }

    @Override
    public List<ConsultationVO> getConsultationsByUserId(Long userId, String status) {
        List<Consultation> consultations;
        if (status != null && !status.isEmpty()) {
            consultations = baseMapper.selectByUserIdAndStatus(userId, status);
        } else {
            consultations = baseMapper.selectByUserId(userId);
        }

        if (consultations.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> doctorIds = consultations.stream()
                .map(Consultation::getDoctorId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Doctor> doctorMap = doctorMapper.selectBatchIds(doctorIds).stream()
                .collect(Collectors.toMap(Doctor::getId, d -> d));

        return consultations.stream()
                .map(c -> convertToVO(c, doctorMap.get(c.getDoctorId())))
                .collect(Collectors.toList());
    }

    @Override
    public ConsultationVO getConsultationById(Long id) {
        Consultation consultation = baseMapper.selectById(id);
        if (consultation == null) {
            return null;
        }
        return convertToVO(consultation);
    }

    @Override
    @Transactional
    public ConsultationVO cancelConsultation(Long id, Long userId) {
        Consultation consultation = baseMapper.selectById(id);
        if (consultation == null) {
            return null;
        }
        
        if (!consultation.getUserId().equals(userId)) {
            return null;
        }
        
        if (!"pending".equals(consultation.getStatus())) {
            return null;
        }
        
        consultation.setStatus("closed");
        consultation.setUpdatedAt(LocalDateTime.now());
        baseMapper.updateById(consultation);
        
        return convertToVO(consultation);
    }

    private ConsultationVO convertToVO(Consultation consultation) {
        Doctor doctor = doctorMapper.selectById(consultation.getDoctorId());
        return convertToVO(consultation, doctor);
    }

    private ConsultationVO convertToVO(Consultation consultation, Doctor doctor) {
        ConsultationVO vo = new ConsultationVO();
        vo.setId(consultation.getId());
        vo.setConsultationNo(consultation.getConsultationNo());
        vo.setConsultationType(consultation.getConsultationType());
        vo.setSymptom(consultation.getSymptom());
        vo.setDescription(consultation.getDescription());
        vo.setImages(consultation.getImages());
        vo.setMedicalHistory(consultation.getMedicalHistory());
        vo.setCurrentMedication(consultation.getCurrentMedication());
        vo.setStatus(consultation.getStatus());
        vo.setStatusText(STATUS_MAP.getOrDefault(consultation.getStatus(), consultation.getStatus()));
        vo.setCreatedAt(consultation.getCreatedAt());

        if (doctor != null) {
            ConsultationVO.DoctorVO doctorVO = new ConsultationVO.DoctorVO();
            doctorVO.setId(doctor.getId());
            doctorVO.setName(doctor.getName());
            doctorVO.setTitle(doctor.getTitle());
            doctorVO.setAvatar(doctor.getAvatar());
            vo.setDoctor(doctorVO);
        }

        return vo;
    }

    private String generateConsultationNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long sequence = System.currentTimeMillis() % 1000000;
        return "C" + timestamp + String.format("%06d", sequence);
    }
}