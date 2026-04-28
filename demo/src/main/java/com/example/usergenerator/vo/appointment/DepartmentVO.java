package com.example.usergenerator.vo.appointment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentVO {

    private Long id;

    private String name;

    private String description;

    private Integer doctorsCount;
}