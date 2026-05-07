package com.example.usergenerator.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.usergenerator.annotation.RequirePermission;
import com.example.usergenerator.common.Result;
import com.example.usergenerator.constant.RoleConstants;
import com.example.usergenerator.dto.drug.DrugCreateDTO;
import com.example.usergenerator.dto.drug.DrugQueryDTO;
import com.example.usergenerator.dto.drug.DrugUpdateDTO;
import com.example.usergenerator.service.DrugService;
import com.example.usergenerator.vo.drug.DrugVO;
import com.example.usergenerator.vo.drug.DrugCategoryVO;
import com.example.usergenerator.vo.drug.InventorySummaryVO;
import com.example.usergenerator.vo.drug.InventoryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/drug")
@RequiredArgsConstructor
@Validated
public class DrugController {

    private final DrugService drugService;

    // 药品管理
    @PostMapping("/create")
    @RequirePermission(RoleConstants.ADMIN)
    public Result<DrugVO> createDrug(@Valid @RequestBody DrugCreateDTO dto) {
        DrugVO vo = drugService.createDrug(dto);
        return Result.success("创建药品成功", vo);
    }

    @PutMapping("/update")
    @RequirePermission(RoleConstants.ADMIN)
    public Result<DrugVO> updateDrug(@Valid @RequestBody DrugUpdateDTO dto) {
        DrugVO vo = drugService.updateDrug(dto);
        return Result.success("更新药品成功", vo);
    }

    @DeleteMapping("/delete/{id}")
    @RequirePermission(RoleConstants.ADMIN)
    public Result<Void> deleteDrug(@PathVariable @NotNull(message = "药品ID不能为空") String id) {
        drugService.deleteDrug(id);
        return Result.success("删除药品成功");
    }

    @GetMapping("/detail/{id}")
    @RequirePermission({RoleConstants.USER, RoleConstants.DOCTOR})
    public Result<DrugVO> getDrugDetail(@PathVariable @NotNull(message = "药品ID不能为空") String id) {
        DrugVO vo = drugService.getDrugById(id);
        return Result.success("查询药品成功", vo);
    }

    @PostMapping("/list")
    @RequirePermission({RoleConstants.USER, RoleConstants.DOCTOR})
    public Result<IPage<DrugVO>> getDrugList(@RequestBody DrugQueryDTO query) {
        IPage<DrugVO> page = drugService.getDrugList(query);
        return Result.success("查询药品列表成功", page);
    }

    
    @GetMapping("/search")
    @RequirePermission({RoleConstants.USER, RoleConstants.DOCTOR})
    public Result<List<DrugVO>> searchDrugs(
            @RequestParam(required = false) String drugName,
            @RequestParam(defaultValue = "50") int size) {
        DrugQueryDTO query = new DrugQueryDTO();
        query.setDrugName(drugName);
        query.setSize(size);
        IPage<DrugVO> result = drugService.getDrugList(query);
        return Result.success(result.getRecords());
    }

    // 库存管理
    @GetMapping("/inventory/summary")
    @RequirePermission(RoleConstants.USER)
    public Result<InventorySummaryVO> getInventorySummary() {
        InventorySummaryVO summary = drugService.getInventorySummary();
        return Result.success("查询库存概览成功", summary);
    }

    @GetMapping("/inventory/list")
    @RequirePermission(RoleConstants.USER)
    public Result<IPage<InventoryVO>> getInventoryList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        IPage<InventoryVO> inventoryList = drugService.getInventoryList(page, size);
        return Result.success("查询库存列表成功", inventoryList);
    }

    // 分类管理
    @GetMapping("/categories")
    @RequirePermission(RoleConstants.USER)
    public Result<List<DrugCategoryVO>> getDrugCategories() {
        List<DrugCategoryVO> categories = drugService.getDrugCategories();
        return Result.success("查询药品分类成功", categories);
    }
}
