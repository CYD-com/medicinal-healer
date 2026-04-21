package com.example.usergenerator.vo.drug;

import lombok.Data;

@Data
public class InventorySummaryVO {

    private Integer totalDrugs;
    private Integer lowStock;
    private Integer outOfStock;
    private Integer expiringSoon;
}
