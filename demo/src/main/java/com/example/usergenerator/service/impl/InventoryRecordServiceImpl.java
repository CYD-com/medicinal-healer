package com.example.usergenerator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.usergenerator.dto.drug.InventoryChangeDTO;
import com.example.usergenerator.entity.Drug;
import com.example.usergenerator.entity.Inventory;
import com.example.usergenerator.entity.InventoryRecord;
import com.example.usergenerator.exception.BusinessException;
import com.example.usergenerator.mapper.DrugMapper;
import com.example.usergenerator.mapper.InventoryMapper;
import com.example.usergenerator.mapper.InventoryRecordMapper;
import com.example.usergenerator.service.InventoryRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryRecordServiceImpl implements InventoryRecordService {

    private final InventoryRecordMapper inventoryRecordMapper;
    private final InventoryMapper inventoryMapper;
    private final DrugMapper drugMapper;

    @Override
    @Transactional
    public void recordChange(InventoryChangeDTO dto, Long operatorId) {
        QueryWrapper<Inventory> invWrapper = new QueryWrapper<>();
        invWrapper.eq("drug_id", dto.getDrugId());
        Inventory inventory = inventoryMapper.selectOne(invWrapper);
        if (inventory == null) {
            throw new BusinessException("库存记录不存在");
        }

        int beforeQuantity = inventory.getStock();
        int afterQuantity;

        switch (dto.getType()) {
            case "in":
                afterQuantity = beforeQuantity + dto.getQuantity();
                break;
            case "out":
                if (beforeQuantity < dto.getQuantity()) {
                    throw new BusinessException("库存不足，当前库存: " + beforeQuantity);
                }
                afterQuantity = beforeQuantity - dto.getQuantity();
                break;
            case "adjust":
                afterQuantity = dto.getQuantity();
                break;
            default:
                throw new BusinessException("无效的变动类型: " + dto.getType());
        }

        inventory.setStock(afterQuantity);
        inventoryMapper.updateById(inventory);

        InventoryRecord record = new InventoryRecord();
        record.setDrugId(dto.getDrugId());
        record.setInventoryId(inventory.getId());
        record.setChangeType(dto.getType());
        record.setChangeQuantity(dto.getQuantity());
        record.setBeforeQuantity(beforeQuantity);
        record.setAfterQuantity(afterQuantity);
        record.setOperatorId(operatorId);
        record.setReason(dto.getReason());
        inventoryRecordMapper.insert(record);

        if (afterQuantity <= inventory.getSafetyStock()) {
            Drug drug = drugMapper.selectById(dto.getDrugId());
            String drugName = drug != null ? drug.getDrugName() : "未知药品";
            log.warn("药品库存预警: {} 当前库存: {} 警戒线: {}", drugName, afterQuantity, inventory.getSafetyStock());
        }

        log.info("库存变动: drugId={}, type={}, quantity={}, before={}, after={}", dto.getDrugId(), dto.getType(), dto.getQuantity(), beforeQuantity, afterQuantity);
    }

    @Override
    public Map<String, Object> getRecordsByDrugId(String drugId, Integer page, Integer size) {
        QueryWrapper<InventoryRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("drug_id", drugId).orderByDesc("create_time");
        List<InventoryRecord> all = inventoryRecordMapper.selectList(wrapper);
        int total = all.size();
        int from = (page - 1) * size;
        int to = Math.min(from + size, total);
        List<InventoryRecord> records = from < total ? all.subList(from, to) : Collections.emptyList();

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", total);
        return result;
    }
}
