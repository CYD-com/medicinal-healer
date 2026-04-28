package com.example.usergenerator.dto.appointment;

import lombok.Data;

@Data
public class AppointmentUpdateDTO {

    private Long id;

    private String status;

    private String patientName;

    private String patientPhone;

    private String symptoms;
}