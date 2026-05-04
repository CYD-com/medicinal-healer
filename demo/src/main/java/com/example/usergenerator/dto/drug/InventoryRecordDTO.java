package com.example.usergenerator.dto.drug;

import lombok.Data;
import javax.validation.constraints.*;

@Data
public class InventoryRecordDTO {

    @NotBlank(message = "药品ID不能为空")
    private String drugId;

    @NotBlank(message = "变动类型不能为空")
    private String changeType;

    @NotNull(message = "变动数量不能为空")
    private Integer changeQuantity;

    private String reason;
}
