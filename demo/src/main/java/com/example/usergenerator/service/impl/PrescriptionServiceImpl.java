package com.example.usergenerator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.usergenerator.common.ResultCode;
import com.example.usergenerator.dto.prescription.PrescriptionCreateDTO;
import com.example.usergenerator.dto.prescription.PrescriptionQueryDTO;
import com.example.usergenerator.entity.Department;
import com.example.usergenerator.entity.Doctor;
import com.example.usergenerator.entity.Prescription;
import com.example.usergenerator.entity.PrescriptionItem;
import com.example.usergenerator.exception.BusinessException;
import com.example.usergenerator.mapper.DepartmentMapper;
import com.example.usergenerator.mapper.DoctorMapper;
import com.example.usergenerator.mapper.PrescriptionItemMapper;
import com.example.usergenerator.mapper.PrescriptionMapper;
import com.example.usergenerator.service.PrescriptionService;
import com.example.usergenerator.vo.prescription.PrescriptionItemVO;
import com.example.usergenerator.vo.prescription.PrescriptionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrescriptionServiceImpl extends ServiceImpl<PrescriptionMapper, Prescription> implements PrescriptionService {

    private final PrescriptionItemMapper prescriptionItemMapper;
    private final DoctorMapper doctorMapper;
    private final DepartmentMapper departmentMapper;

    private static final Map<String, String> STATUS_MAP = Map.of(
            "pending", "待审核",
            "approved", "已审核",
            "rejected", "已拒绝",
            "completed", "已完成",
            "expired", "已过期"
    );

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PrescriptionVO createPrescription(PrescriptionCreateDTO dto, Long userId) {
        Prescription prescription = new Prescription();
        prescription.setPrescriptionNo(generatePrescriptionNo());
        prescription.setUserId(userId);
        prescription.setDoctorId(dto.getDoctorId());
        prescription.setDiagnosis(dto.getDiagnosis());
        prescription.setDoctorAdvice(dto.getDoctorAdvice());
        prescription.setStatus("pending");
        prescription.setCreatedAt(LocalDateTime.now());
        prescription.setUpdatedAt(LocalDateTime.now());
        prescription.setDeleted(0);

        BigDecimal totalAmount = BigDecimal.ZERO;
        int drugCount = 0;

        baseMapper.insert(prescription);

        for (PrescriptionCreateDTO.PrescriptionItemDTO itemDTO : dto.getItems()) {
            PrescriptionItem item = new PrescriptionItem();
            item.setPrescriptionId(prescription.getId());
            item.setDrugId(itemDTO.getDrugId());
            item.setDrugName(itemDTO.getDrugName());
            item.setSpecification(itemDTO.getSpecification());
            item.setDosage(itemDTO.getDosage());
            item.setQuantity(itemDTO.getQuantity());
            item.setUnit(itemDTO.getUnit());
            BigDecimal unitPrice = itemDTO.getUnitPrice() != null ? BigDecimal.valueOf(itemDTO.getUnitPrice()) : BigDecimal.ZERO;
            item.setUnitPrice(unitPrice);

            BigDecimal amount = itemDTO.getAmount() != null
                    ? BigDecimal.valueOf(itemDTO.getAmount())
                    : unitPrice.multiply(BigDecimal.valueOf(itemDTO.getQuantity()));
            item.setAmount(amount);
            item.setCreatedAt(LocalDateTime.now());

            prescriptionItemMapper.insert(item);

            totalAmount = totalAmount.add(amount);
            drugCount += itemDTO.getQuantity();
        }

        prescription.setTotalAmount(totalAmount);
        prescription.setDrugCount(drugCount);
        prescription.setValidUntil(LocalDateTime.now().plusDays(7));
        baseMapper.updateById(prescription);

        log.info("创建处方成功，编号：{}", prescription.getPrescriptionNo());
        return convertToVO(prescription);
    }

    @Override
    public List<PrescriptionVO> getPrescriptionsByUserId(Long userId, String status) {
        List<Prescription> prescriptions;
        if (StringUtils.hasText(status)) {
            prescriptions = baseMapper.selectByUserIdAndStatus(userId, status);
        } else {
            prescriptions = baseMapper.selectByUserId(userId);
        }

        if (prescriptions.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> doctorIds = prescriptions.stream()
                .map(Prescription::getDoctorId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Doctor> doctorMap = doctorMapper.selectBatchIds(doctorIds).stream()
                .collect(Collectors.toMap(Doctor::getId, d -> d));

        List<Long> prescriptionIds = prescriptions.stream()
                .map(Prescription::getId)
                .collect(Collectors.toList());

        LambdaQueryWrapper<PrescriptionItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.in(PrescriptionItem::getPrescriptionId, prescriptionIds);
        List<PrescriptionItem> allItems = prescriptionItemMapper.selectList(itemWrapper);

        Map<Long, List<PrescriptionItem>> itemMap = allItems.stream()
                .collect(Collectors.groupingBy(PrescriptionItem::getPrescriptionId));

        List<PrescriptionVO> result = new ArrayList<>();
        for (Prescription p : prescriptions) {
            Doctor doctor = doctorMap.get(p.getDoctorId());
            List<PrescriptionItem> items = itemMap.getOrDefault(p.getId(), new ArrayList<>());
            result.add(convertToVO(p, doctor, items));
        }

        return result;
    }

    @Override
    public PrescriptionVO getPrescriptionById(Long id) {
        Prescription prescription = baseMapper.selectById(id);
        if (prescription == null) {
            throw new BusinessException(ResultCode.PRESCRIPTION_NOT_FOUND);
        }

        Doctor doctor = doctorMapper.selectById(prescription.getDoctorId());

        LambdaQueryWrapper<PrescriptionItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(PrescriptionItem::getPrescriptionId, id);
        List<PrescriptionItem> items = prescriptionItemMapper.selectList(itemWrapper);

        return convertToVO(prescription, doctor, items);
    }

    @Override
    public List<PrescriptionVO> getPrescriptionList(PrescriptionQueryDTO query) {
        LambdaQueryWrapper<Prescription> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Prescription::getDeleted, 0);

        if (StringUtils.hasText(query.getPrescriptionNo())) {
            wrapper.like(Prescription::getPrescriptionNo, query.getPrescriptionNo());
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(Prescription::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(Prescription::getCreatedAt);

        List<Prescription> prescriptions = baseMapper.selectList(wrapper);

        if (prescriptions.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> doctorIds = prescriptions.stream()
                .map(Prescription::getDoctorId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Doctor> doctorMap = doctorMapper.selectBatchIds(doctorIds).stream()
                .collect(Collectors.toMap(Doctor::getId, d -> d));

        List<Long> prescriptionIds = prescriptions.stream()
                .map(Prescription::getId)
                .collect(Collectors.toList());

        LambdaQueryWrapper<PrescriptionItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.in(PrescriptionItem::getPrescriptionId, prescriptionIds);
        List<PrescriptionItem> allItems = prescriptionItemMapper.selectList(itemWrapper);

        Map<Long, List<PrescriptionItem>> itemMap = allItems.stream()
                .collect(Collectors.groupingBy(PrescriptionItem::getPrescriptionId));

        List<PrescriptionVO> result = new ArrayList<>();
        for (Prescription p : prescriptions) {
            Doctor doctor = doctorMap.get(p.getDoctorId());
            List<PrescriptionItem> items = itemMap.getOrDefault(p.getId(), new ArrayList<>());
            result.add(convertToVO(p, doctor, items));
        }

        return result;
    }

    private PrescriptionVO convertToVO(Prescription prescription) {
        Doctor doctor = doctorMapper.selectById(prescription.getDoctorId());

        LambdaQueryWrapper<PrescriptionItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(PrescriptionItem::getPrescriptionId, prescription.getId());
        List<PrescriptionItem> items = prescriptionItemMapper.selectList(itemWrapper);

        return convertToVO(prescription, doctor, items);
    }

    private PrescriptionVO convertToVO(Prescription prescription, Doctor doctor, List<PrescriptionItem> items) {
        PrescriptionVO vo = new PrescriptionVO();
        vo.setId(prescription.getId());
        vo.setPrescriptionNo(prescription.getPrescriptionNo());
        vo.setDiagnosis(prescription.getDiagnosis());
        vo.setDoctorAdvice(prescription.getDoctorAdvice());
        vo.setTotalAmount(prescription.getTotalAmount());
        vo.setDrugCount(prescription.getDrugCount());
        vo.setStatus(prescription.getStatus());
        vo.setStatusText(STATUS_MAP.getOrDefault(prescription.getStatus(), prescription.getStatus()));
        vo.setValidUntil(prescription.getValidUntil());
        vo.setCreatedAt(prescription.getCreatedAt());

        if (doctor != null) {
            PrescriptionVO.DoctorInfo doctorInfo = new PrescriptionVO.DoctorInfo();
            doctorInfo.setId(doctor.getId());
            doctorInfo.setName(doctor.getName());
            doctorInfo.setTitle(doctor.getTitle());
            doctorInfo.setAvatar(doctor.getAvatar());

            Department department = departmentMapper.selectById(doctor.getDepartmentId());
            if (department != null) {
                doctorInfo.setDepartment(department.getName());
            }

            vo.setDoctor(doctorInfo);
        }

        if (items != null && !items.isEmpty()) {
            List<PrescriptionItemVO> drugVOs = items.stream().map(this::convertToItemVO).collect(Collectors.toList());
            vo.setDrugs(drugVOs);
        } else {
            vo.setDrugs(new ArrayList<>());
        }

        return vo;
    }

    private PrescriptionItemVO convertToItemVO(PrescriptionItem item) {
        PrescriptionItemVO vo = new PrescriptionItemVO();
        vo.setId(item.getId());
        vo.setDrugId(item.getDrugId());
        vo.setDrugName(item.getDrugName());
        vo.setSpecification(item.getSpecification());
        vo.setDosage(item.getDosage());
        vo.setQuantity(item.getQuantity());
        vo.setUnit(item.getUnit());
        vo.setUnitPrice(item.getUnitPrice());
        vo.setAmount(item.getAmount());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PrescriptionVO updatePrescriptionStatus(Long id, String status) {
        Prescription prescription = baseMapper.selectById(id);
        if (prescription == null) {
            throw new BusinessException(ResultCode.PRESCRIPTION_NOT_FOUND);
        }

        prescription.setStatus(status);
        prescription.setUpdatedAt(LocalDateTime.now());
        baseMapper.updateById(prescription);

        log.info("更新处方状态成功，ID：{}，新状态：{}", id, status);
        return convertToVO(prescription);
    }

    private String generatePrescriptionNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long sequence = System.currentTimeMillis() % 1000000;
        return "P" + timestamp + String.format("%06d", sequence);
    }
}
