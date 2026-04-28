package com.example.usergenerator.dto.appointment;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class AppointmentCreateDTO {

    @NotNull(message = "医生ID不能为空")
    private Long doctorId;

    @NotNull(message = "科室ID不能为空")
    private Long departmentId;

    @NotNull(message = "预约日期不能为空")
    private LocalDate appointmentDate;

    @NotBlank(message = "开始时间不能为空")
    private String startTime;

    @NotBlank(message = "结束时间不能为空")
    private String endTime;

    @NotBlank(message = "患者姓名不能为空")
    private String patientName;

    @NotBlank(message = "患者电话不能为空")
    private String patientPhone;

    private String symptoms;
}