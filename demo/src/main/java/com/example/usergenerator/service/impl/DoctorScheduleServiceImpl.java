package com.example.usergenerator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.usergenerator.dto.schedule.DoctorScheduleDTO;
import com.example.usergenerator.dto.schedule.ScheduleQueryDTO;
import com.example.usergenerator.entity.Doctor;
import com.example.usergenerator.entity.DoctorSchedule;
import com.example.usergenerator.exception.BusinessException;
import com.example.usergenerator.mapper.DoctorMapper;
import com.example.usergenerator.mapper.DoctorScheduleMapper;
import com.example.usergenerator.service.DoctorScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DoctorScheduleServiceImpl implements DoctorScheduleService {

    private final DoctorScheduleMapper doctorScheduleMapper;
    private final DoctorMapper doctorMapper;

    @Override
    @Transactional
    public DoctorSchedule createSchedule(DoctorScheduleDTO dto) {
        Doctor doctor = doctorMapper.selectById(dto.getDoctorId());
        if (doctor == null) {
            throw new BusinessException("医生不存在");
        }

        if (dto.getDepartmentId() == null) {
            dto.setDepartmentId(doctor.getDepartmentId());
        }

        List<DoctorSchedule> existing = doctorScheduleMapper.findByDoctorAndDate(dto.getDoctorId(), dto.getWorkDate());
        boolean conflict = existing.stream().anyMatch(s ->
                dto.getStartTime().compareTo(s.getEndTime()) < 0 && dto.getEndTime().compareTo(s.getStartTime()) > 0);
        if (conflict) {
            throw new BusinessException("该时段与已有排班冲突");
        }

        DoctorSchedule schedule = new DoctorSchedule();
        BeanUtils.copyProperties(dto, schedule);
        schedule.setCurrentAppointments(0);
        schedule.setStatus(1);
        doctorScheduleMapper.insert(schedule);

        log.info("创建排班成功，医生ID: {}, 日期: {}, 时段: {}-{}", dto.getDoctorId(), dto.getWorkDate(), dto.getStartTime(), dto.getEndTime());
        return schedule;
    }

    @Override
    public List<DoctorSchedule> getSchedulesByDoctor(Long doctorId, LocalDate startDate, LocalDate endDate) {
        return doctorScheduleMapper.findByDoctorAndDateRange(doctorId, startDate, endDate);
    }

    @Override
    public List<DoctorSchedule> getSchedulesByDepartment(Long departmentId, LocalDate workDate) {
        return doctorScheduleMapper.findByDepartmentAndDate(departmentId, workDate);
    }

    @Override
    public Map<String, Object> getScheduleList(ScheduleQueryDTO query) {
        QueryWrapper<DoctorSchedule> wrapper = new QueryWrapper<>();
        if (query.getDoctorId() != null) {
            wrapper.eq("doctor_id", query.getDoctorId());
        }
        if (query.getDepartmentId() != null) {
            wrapper.eq("department_id", query.getDepartmentId());
        }
        if (query.getStartDate() != null) {
            wrapper.ge("work_date", query.getStartDate());
        }
        if (query.getEndDate() != null) {
            wrapper.le("work_date", query.getEndDate());
        }
        wrapper.orderByAsc("work_date").orderByAsc("start_time");

        List<DoctorSchedule> allRecords = doctorScheduleMapper.selectList(wrapper);
        int total = allRecords.size();
        int fromIndex = (query.getPage() - 1) * query.getSize();
        int toIndex = Math.min(fromIndex + query.getSize(), total);
        List<DoctorSchedule> pageRecords = fromIndex < total ? allRecords.subList(fromIndex, toIndex) : Collections.emptyList();

        Map<String, Object> result = new HashMap<>();
        result.put("records", pageRecords);
        result.put("total", total);
        result.put("page", query.getPage());
        result.put("size", query.getSize());
        return result;
    }

    @Override
    @Transactional
    public boolean updateStatus(Long id, Integer status) {
        DoctorSchedule schedule = doctorScheduleMapper.selectById(id);
        if (schedule == null) {
            throw new BusinessException("排班不存在");
        }
        schedule.setStatus(status);
        return doctorScheduleMapper.updateById(schedule) > 0;
    }

    @Override
    @Transactional
    public boolean deleteSchedule(Long id) {
        DoctorSchedule schedule = doctorScheduleMapper.selectById(id);
        if (schedule == null) {
            throw new BusinessException("排班不存在");
        }
        if (schedule.getCurrentAppointments() > 0) {
            throw new BusinessException("该排班已有预约，无法删除");
        }
        return doctorScheduleMapper.deleteById(id) > 0;
    }

    @Override
    public boolean checkAvailability(Long doctorId, LocalDate workDate, String timeSlot) {
        List<DoctorSchedule> schedules = doctorScheduleMapper.findByDoctorAndDate(doctorId, workDate);
        String[] slotParts = timeSlot.split("-");
        String requestStart = slotParts[0];
        String requestEnd = slotParts.length > 1 ? slotParts[1] : "";

        for (DoctorSchedule schedule : schedules) {
            if (requestStart.compareTo(schedule.getEndTime()) < 0 && requestEnd.compareTo(schedule.getStartTime()) > 0) {
                if (schedule.getCurrentAppointments() >= schedule.getMaxAppointments()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public List<Map<String, Object>> getAvailableTimeSlots(Long doctorId, LocalDate workDate) {
        List<DoctorSchedule> schedules = doctorScheduleMapper.findByDoctorAndDate(doctorId, workDate);
        List<Map<String, Object>> slots = new ArrayList<>();

        for (DoctorSchedule schedule : schedules) {
            Map<String, Object> slot = new HashMap<>();
            slot.put("scheduleId", schedule.getId());
            slot.put("startTime", schedule.getStartTime());
            slot.put("endTime", schedule.getEndTime());
            slot.put("maxAppointments", schedule.getMaxAppointments());
            slot.put("currentAppointments", schedule.getCurrentAppointments());
            slot.put("available", schedule.getCurrentAppointments() < schedule.getMaxAppointments());
            slot.put("remainingSlots", schedule.getMaxAppointments() - schedule.getCurrentAppointments());
            slots.add(slot);
        }
        return slots;
    }
}
