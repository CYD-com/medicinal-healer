package com.example.usergenerator.dto.prescription;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class PrescriptionCreateDTO {

    private Long userId;

    private Long doctorId;

    @NotBlank(message = "诊断不能为空")
    private String diagnosis;

    private String doctorAdvice;

    @NotEmpty(message = "处方药品不能为空")
    @Valid
    private List<PrescriptionItemDTO> items;

    @Data
    public static class PrescriptionItemDTO {

        @NotBlank(message = "药品ID不能为空")
        private String drugId;

        @NotBlank(message = "药品名称不能为空")
        private String drugName;

        private String specification;

        private String dosage;

        private Integer quantity = 1;

        private String unit = "盒";

        private Double unitPrice;

        private Double amount;
    }
}
