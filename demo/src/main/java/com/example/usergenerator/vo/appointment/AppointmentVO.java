package com.example.usergenerator.vo.appointment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentVO {

    private Long appointmentId;

    private LocalDate appointmentDate;

    private TimeSlotVO timeSlot;

    private DoctorVO doctor;

    private String department;

    private String status;

    private String patientName;

    private String patientPhone;

    private String symptoms;
}