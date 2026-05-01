package com.example.usergenerator.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.usergenerator.dto.prescription.PrescriptionCreateDTO;
import com.example.usergenerator.dto.prescription.PrescriptionQueryDTO;
import com.example.usergenerator.entity.Prescription;
import com.example.usergenerator.vo.prescription.PrescriptionVO;

import java.util.List;

public interface PrescriptionService extends IService<Prescription> {

    PrescriptionVO createPrescription(PrescriptionCreateDTO dto, Long userId);

    List<PrescriptionVO> getPrescriptionsByUserId(Long userId, String status);

    PrescriptionVO getPrescriptionById(Long id);

    List<PrescriptionVO> getPrescriptionList(PrescriptionQueryDTO query);

    PrescriptionVO updatePrescriptionStatus(Long id, String status);
}
