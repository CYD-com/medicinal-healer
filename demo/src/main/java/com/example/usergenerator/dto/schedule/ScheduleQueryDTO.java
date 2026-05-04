package com.example.usergenerator.dto.schedule;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ScheduleQueryDTO {

    private Long doctorId;

    private Long departmentId;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer page = 1;

    private Integer size = 10;
}
