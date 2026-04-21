package com.example.usergenerator.vo.record;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordVO {

    private Long id;

    private String category;

    private String type;

    private String status;

    private BigDecimal amount;

    private LocalDate time;
}
