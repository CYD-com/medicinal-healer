package com.example.usergenerator.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.usergenerator.dto.prescription.PrescriptionCreateDTO;
import com.example.usergenerator.dto.prescription.PrescriptionQueryDTO;
import com.example.usergenerator.entity.Prescription;
import com.example.usergenerator.vo.prescription.PrescriptionVO;

import java.util.List;

public interface PrescriptionService extends IService<Prescription> {

    PrescriptionVO createPrescription(PrescriptionCreateDTO dto, Long userId);

    List<PrescriptionVO> getPrescriptionsByUserId(Long userId, String status);

    IPage<PrescriptionVO> getPrescriptionsByUserIdPage(Long userId, String status, Page<Prescription> page);

    List<PrescriptionVO> getPrescriptionsByDoctorUserId(Long userId, String status);

    IPage<PrescriptionVO> getPrescriptionsByDoctorUserIdPage(Long userId, String status, String prescriptionNo, Page<Prescription> page);

    PrescriptionVO getPrescriptionById(Long id);

    List<PrescriptionVO> getPrescriptionList(PrescriptionQueryDTO query);

    IPage<PrescriptionVO> getPrescriptionListPage(PrescriptionQueryDTO query, Page<Prescription> page);

    PrescriptionVO updatePrescriptionStatus(Long id, String status);

    boolean deletePrescription(Long id);
}
