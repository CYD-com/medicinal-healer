package com.example.usergenerator.dto.appointment;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AppointmentQueryDTO {

    private Long userId;

    private Long doctorId;

    private Long departmentId;

    private String status;

    private LocalDate appointmentDate;

    private Integer page = 1;

    private Integer size = 10;
}