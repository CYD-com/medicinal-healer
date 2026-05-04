package com.example.usergenerator.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.usergenerator.entity.DoctorSchedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface DoctorScheduleMapper extends BaseMapper<DoctorSchedule> {

    @Select("SELECT * FROM doctor_schedule WHERE doctor_id = #{doctorId} AND work_date = #{workDate} AND status = 1")
    List<DoctorSchedule> findByDoctorAndDate(@Param("doctorId") Long doctorId, @Param("workDate") LocalDate workDate);

    @Select("SELECT * FROM doctor_schedule WHERE doctor_id = #{doctorId} AND work_date BETWEEN #{startDate} AND #{endDate} AND status = 1 ORDER BY work_date, start_time")
    List<DoctorSchedule> findByDoctorAndDateRange(@Param("doctorId") Long doctorId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Select("SELECT * FROM doctor_schedule WHERE department_id = #{departmentId} AND work_date = #{workDate} AND status = 1 ORDER BY start_time")
    List<DoctorSchedule> findByDepartmentAndDate(@Param("departmentId") Long departmentId, @Param("workDate") LocalDate workDate);

    @Select("SELECT ds.*, d.real_name as doctor_name FROM doctor_schedule ds LEFT JOIN sys_user d ON ds.doctor_id = d.id WHERE ds.work_date = #{workDate} AND ds.status = 1 ORDER BY ds.start_time")
    List<DoctorSchedule> findSchedulesByDate(@Param("workDate") LocalDate workDate);
}
