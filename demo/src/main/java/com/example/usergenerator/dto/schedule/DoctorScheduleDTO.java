package com.example.usergenerator.dto.schedule;

import lombok.Data;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class DoctorScheduleDTO {

    @NotNull(message = "医生ID不能为空")
    private Long doctorId;

    private Long departmentId;

    @NotNull(message = "工作日期不能为空")
    @Future(message = "工作日期必须是未来日期")
    private LocalDate workDate;

    @NotBlank(message = "开始时间不能为空")
    private String startTime;

    @NotBlank(message = "结束时间不能为空")
    private String endTime;

    @Min(value = 1, message = "最大预约数至少为1")
    private Integer maxAppointments = 20;

    private String remark;
}
