package com.example.usergenerator.controller;

import com.example.usergenerator.common.Result;
import com.example.usergenerator.dto.drug.InventoryChangeDTO;
import com.example.usergenerator.service.InventoryRecordService;
import com.example.usergenerator.util.PermissionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/drug/inventory")
@RequiredArgsConstructor
public class InventoryRecordController {

    private final InventoryRecordService inventoryRecordService;
    private final PermissionUtil permissionUtil;

    @PostMapping("/change")
    public ResponseEntity<?> changeInventory(@Valid @RequestBody InventoryChangeDTO dto) {
        Long operatorId = permissionUtil.getCurrentUserId();
        inventoryRecordService.recordChange(dto, operatorId);
        return ResponseEntity.ok(Result.success("库存变动成功"));
    }

    @GetMapping("/records/{drugId}")
    public ResponseEntity<?> getRecords(
            @PathVariable String drugId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Map<String, Object> result = inventoryRecordService.getRecordsByDrugId(drugId, page, size);
        return ResponseEntity.ok(Result.success(result));
    }
}
