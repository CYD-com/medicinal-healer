package com.example.usergenerator.dto.record;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RecordCreateDTO {

    @NotNull(message = "分类不能为空")
    private String category;

    @NotNull(message = "类型不能为空")
    private String type;

    @NotNull(message = "状态不能为空")
    private String status;

    @NotNull(message = "金额不能为空")
    private BigDecimal amount;

    private LocalDate time;
}
