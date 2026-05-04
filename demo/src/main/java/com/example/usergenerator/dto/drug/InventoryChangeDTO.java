package com.example.usergenerator.dto.drug;

import lombok.Data;
import javax.validation.constraints.*;

@Data
public class InventoryChangeDTO {

    @NotBlank(message = "药品ID不能为空")
    private String drugId;

    @NotNull(message = "变动数量不能为空")
    private Integer quantity;

    @NotBlank(message = "变动类型不能为空")
    private String type;

    private String reason;
}
