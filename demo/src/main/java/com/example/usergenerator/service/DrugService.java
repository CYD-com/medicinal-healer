package com.example.usergenerator.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.usergenerator.dto.drug.DrugCreateDTO;
import com.example.usergenerator.dto.drug.DrugQueryDTO;
import com.example.usergenerator.dto.drug.DrugUpdateDTO;
import com.example.usergenerator.vo.drug.DrugVO;
import com.example.usergenerator.vo.drug.DrugCategoryVO;
import com.example.usergenerator.vo.drug.InventorySummaryVO;
import com.example.usergenerator.vo.drug.InventoryVO;

import java.util.List;

public interface DrugService {

    // 药品管理
    DrugVO createDrug(DrugCreateDTO dto);
    DrugVO updateDrug(DrugUpdateDTO dto);
    void deleteDrug(String id);
    DrugVO getDrugById(String id);
    IPage<DrugVO> getDrugList(DrugQueryDTO query);

    // 库存管理
    InventorySummaryVO getInventorySummary();
    IPage<InventoryVO> getInventoryList(int page, int size);

    // 分类管理
    List<DrugCategoryVO> getDrugCategories();
}
