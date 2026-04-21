package com.example.usergenerator.vo.drug;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DrugCategoryVO {

    private Integer id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
}
