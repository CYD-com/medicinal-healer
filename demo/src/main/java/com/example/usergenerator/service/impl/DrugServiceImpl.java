package com.example.usergenerator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.usergenerator.common.ResultCode;
import com.example.usergenerator.dto.drug.DrugCreateDTO;
import com.example.usergenerator.dto.drug.DrugQueryDTO;
import com.example.usergenerator.dto.drug.DrugUpdateDTO;
import com.example.usergenerator.entity.Drug;
import com.example.usergenerator.entity.DrugCategory;
import com.example.usergenerator.entity.Inventory;
import com.example.usergenerator.exception.BusinessException;
import com.example.usergenerator.mapper.DrugMapper;
import com.example.usergenerator.mapper.DrugCategoryMapper;
import com.example.usergenerator.mapper.InventoryMapper;
import com.example.usergenerator.service.DrugService;
import com.example.usergenerator.vo.drug.DrugVO;
import com.example.usergenerator.vo.drug.DrugCategoryVO;
import com.example.usergenerator.vo.drug.InventorySummaryVO;
import com.example.usergenerator.vo.drug.InventoryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DrugServiceImpl implements DrugService {

    private final DrugMapper drugMapper;
    private final InventoryMapper inventoryMapper;
    private final DrugCategoryMapper drugCategoryMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DrugVO createDrug(DrugCreateDTO dto) {
        Drug drug = new Drug();
        drug.setDrugName(dto.getDrugName());
        drug.setGenericName(dto.getGenericName());
        drug.setSpecification(dto.getSpecification());
        drug.setManufacturer(dto.getManufacturer());
        drug.setCategory(dto.getCategory());
        drug.setType(dto.getType());
        drug.setPrice(dto.getPrice());
        drug.setStock(dto.getStock());
        drug.setUnit(dto.getUnit());
        drug.setEnglishName(dto.getEnglishName());
        drug.setApprovalNo(dto.getApprovalNo());
        drug.setForm(dto.getForm());
        drug.setShelfLife(dto.getShelfLife());
        drug.setStorage(dto.getStorage());
        drug.setIndications(dto.getIndications());
        drug.setContraindications(dto.getContraindications());
        drug.setAdverseReactions(dto.getAdverseReactions());
        drug.setPrecautions(dto.getPrecautions());
        drug.setDrugInteractions(dto.getDrugInteractions());
        drug.setDosage(dto.getDosage());
        drug.setImage(dto.getImage());

        int result = drugMapper.insert(drug);
        if (result == 0) {
            throw new BusinessException(ResultCode.ERROR);
        }

        Inventory inventory = new Inventory();
        inventory.setDrugId(drug.getId());
        inventory.setStock(drug.getStock());
        inventory.setSafetyStock(20);
        inventory.setStatus(drug.getStock() >= 20 ? "normal" : drug.getStock() > 0 ? "low" : "out");
        inventory.setExpiryDate(LocalDate.now().plusYears(2));
        inventory.setDaysToExpiry((int) ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.now().plusYears(2)));
        inventoryMapper.insert(inventory);

        log.info("创建药品成功，ID：{}", drug.getId());
        return convertToDrugVO(drug);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DrugVO updateDrug(DrugUpdateDTO dto) {
        Drug drug = drugMapper.selectById(dto.getId());
        if (drug == null) {
            throw new BusinessException(ResultCode.DRUG_NOT_FOUND);
        }

        drug.setDrugName(dto.getDrugName());
        drug.setGenericName(dto.getGenericName());
        drug.setSpecification(dto.getSpecification());
        drug.setManufacturer(dto.getManufacturer());
        drug.setCategory(dto.getCategory());
        drug.setType(dto.getType());
        drug.setPrice(dto.getPrice());
        drug.setStock(dto.getStock());
        drug.setUnit(dto.getUnit());
        drug.setEnglishName(dto.getEnglishName());
        drug.setApprovalNo(dto.getApprovalNo());
        drug.setForm(dto.getForm());
        drug.setShelfLife(dto.getShelfLife());
        drug.setStorage(dto.getStorage());
        drug.setIndications(dto.getIndications());
        drug.setContraindications(dto.getContraindications());
        drug.setAdverseReactions(dto.getAdverseReactions());
        drug.setPrecautions(dto.getPrecautions());
        drug.setDrugInteractions(dto.getDrugInteractions());
        drug.setDosage(dto.getDosage());
        drug.setImage(dto.getImage());

        int result = drugMapper.updateById(drug);
        if (result == 0) {
            throw new BusinessException(ResultCode.ERROR);
        }

        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Inventory::getDrugId, dto.getId());
        Inventory inventory = inventoryMapper.selectOne(wrapper);
        if (inventory != null) {
            inventory.setStock(dto.getStock());
            inventory.setStatus(dto.getStock() >= 20 ? "normal" : dto.getStock() > 0 ? "low" : "out");
            inventoryMapper.updateById(inventory);
        }

        log.info("更新药品成功，ID：{}", drug.getId());
        return convertToDrugVO(drug);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDrug(String id) {
        Drug drug = drugMapper.selectById(id);
        if (drug == null) {
            throw new BusinessException(ResultCode.DRUG_NOT_FOUND);
        }

        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Inventory::getDrugId, id);
        inventoryMapper.delete(wrapper);

        int result = drugMapper.deleteById(id);
        if (result == 0) {
            throw new BusinessException(ResultCode.ERROR);
        }

        log.info("删除药品成功，ID：{}", id);
    }

    @Override
    public DrugVO getDrugById(String id) {
        Drug drug = drugMapper.selectById(id);
        if (drug == null) {
            throw new BusinessException(ResultCode.DRUG_NOT_FOUND);
        }
        return convertToDrugVO(drug);
    }

    @Override
    public IPage<DrugVO> getDrugList(DrugQueryDTO query) {
        Page<Drug> page = new Page<>(query.getPage(), query.getSize());
        LambdaQueryWrapper<Drug> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getDrugName())) {
            wrapper.like(Drug::getDrugName, query.getDrugName())
                   .or()
                   .like(Drug::getSpecification, query.getDrugName());
        }
        if (StringUtils.hasText(query.getCategory())) {
            wrapper.eq(Drug::getCategory, query.getCategory());
        }
        if (StringUtils.hasText(query.getType())) {
            wrapper.eq(Drug::getType, query.getType());
        }
        wrapper.orderByDesc(Drug::getCreatedAt);
        IPage<Drug> drugPage = drugMapper.selectPage(page, wrapper);
        return drugPage.convert(this::convertToDrugVO);
    }

    @Override
    public InventorySummaryVO getInventorySummary() {
        InventorySummaryVO summary = new InventorySummaryVO();
        summary.setTotalDrugs(drugMapper.selectCount(null).intValue());

        LambdaQueryWrapper<Inventory> lowWrapper = new LambdaQueryWrapper<>();
        lowWrapper.eq(Inventory::getStatus, "low");
        summary.setLowStock(inventoryMapper.selectCount(lowWrapper).intValue());

        LambdaQueryWrapper<Inventory> outWrapper = new LambdaQueryWrapper<>();
        outWrapper.eq(Inventory::getStatus, "out");
        summary.setOutOfStock(inventoryMapper.selectCount(outWrapper).intValue());

        LambdaQueryWrapper<Inventory> expiringWrapper = new LambdaQueryWrapper<>();
        expiringWrapper.le(Inventory::getDaysToExpiry, 90);
        summary.setExpiringSoon(inventoryMapper.selectCount(expiringWrapper).intValue());

        return summary;
    }

    @Override
    public IPage<InventoryVO> getInventoryList(int page, int size) {
        Page<Inventory> inventoryPage = new Page<>(page, size);
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Inventory::getCreatedAt);
        IPage<Inventory> pageResult = inventoryMapper.selectPage(inventoryPage, wrapper);
        return pageResult.convert(this::convertToInventoryVO);
    }

    @Override
    public List<DrugCategoryVO> getDrugCategories() {
        List<DrugCategory> categories = drugCategoryMapper.selectList(null);
        return categories.stream().map(this::convertToDrugCategoryVO).collect(Collectors.toList());
    }

    private DrugVO convertToDrugVO(Drug drug) {
        DrugVO vo = new DrugVO();
        vo.setId(drug.getId());
        vo.setDrugName(drug.getDrugName());
        vo.setGenericName(drug.getGenericName());
        vo.setSpecification(drug.getSpecification());
        vo.setManufacturer(drug.getManufacturer());
        vo.setCategory(drug.getCategory());
        vo.setType(drug.getType());
        vo.setPrice(drug.getPrice());
        vo.setStock(drug.getStock());
        vo.setUnit(drug.getUnit());
        vo.setEnglishName(drug.getEnglishName());
        vo.setApprovalNo(drug.getApprovalNo());
        vo.setForm(drug.getForm());
        vo.setShelfLife(drug.getShelfLife());
        vo.setStorage(drug.getStorage());
        vo.setIndications(drug.getIndications());
        vo.setContraindications(drug.getContraindications());
        vo.setAdverseReactions(drug.getAdverseReactions());
        vo.setPrecautions(drug.getPrecautions());
        vo.setDrugInteractions(drug.getDrugInteractions());
        vo.setDosage(drug.getDosage());
        vo.setImage(drug.getImage());
        vo.setCreatedAt(drug.getCreatedAt());
        vo.setUpdatedAt(drug.getUpdatedAt());
        return vo;
    }

    private InventoryVO convertToInventoryVO(Inventory inventory) {
        InventoryVO vo = new InventoryVO();
        vo.setId(inventory.getId());
        vo.setDrugId(inventory.getDrugId());
        Drug drug = drugMapper.selectById(inventory.getDrugId());
        if (drug != null) {
            vo.setDrugName(drug.getDrugName());
            vo.setSpecification(drug.getSpecification());
        }
        vo.setStock(inventory.getStock());
        vo.setSafetyStock(inventory.getSafetyStock());
        vo.setStatus(inventory.getStatus());
        vo.setExpiryDate(inventory.getExpiryDate());
        vo.setDaysToExpiry(inventory.getDaysToExpiry());
        vo.setCreatedAt(inventory.getCreatedAt());
        vo.setUpdatedAt(inventory.getUpdatedAt());
        return vo;
    }

    private DrugCategoryVO convertToDrugCategoryVO(DrugCategory category) {
        DrugCategoryVO vo = new DrugCategoryVO();
        vo.setId(category.getId());
        vo.setName(category.getName());
        vo.setDescription(category.getDescription());
        vo.setCreatedAt(category.getCreatedAt());
        return vo;
    }
}
