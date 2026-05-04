package com.example.usergenerator.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("doctor_schedule")
public class DoctorSchedule {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long doctorId;

    private Long departmentId;

    private LocalDate workDate;

    private String startTime;

    private String endTime;

    private Integer maxAppointments;

    private Integer currentAppointments;

    private Integer status;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
