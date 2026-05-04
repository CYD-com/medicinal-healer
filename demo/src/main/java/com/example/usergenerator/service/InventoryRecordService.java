package com.example.usergenerator.service;

import com.example.usergenerator.dto.drug.InventoryChangeDTO;
import com.example.usergenerator.entity.InventoryRecord;

import java.util.Map;

public interface InventoryRecordService {

    void recordChange(InventoryChangeDTO dto, Long operatorId);

    Map<String, Object> getRecordsByDrugId(String drugId, Integer page, Integer size);
}
