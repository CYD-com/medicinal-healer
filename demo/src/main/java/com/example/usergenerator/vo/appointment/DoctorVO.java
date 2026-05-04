package com.example.usergenerator.vo.appointment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorVO {

    private Long doctorId;

    private String name;

    private String title;

    private String avatar;

    private Double rating;

    private Integer consultationCount;

    private List<String> specialty;

    private DepartmentVO department;

    private Long userId;
}