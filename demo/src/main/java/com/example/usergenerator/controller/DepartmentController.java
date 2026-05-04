package com.example.usergenerator.controller;

import com.example.usergenerator.annotation.RequirePermission;
import com.example.usergenerator.common.Result;
import com.example.usergenerator.constant.RoleConstants;
import com.example.usergenerator.entity.Department;
import com.example.usergenerator.mapper.DepartmentMapper;
import com.example.usergenerator.vo.appointment.DepartmentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/department")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentMapper departmentMapper;

    @GetMapping("/list")
    @RequirePermission(RoleConstants.ADMIN)
    public Result<List<DepartmentVO>> listDepartments() {
        List<Department> departments = departmentMapper.selectAllDepartments();
        List<DepartmentVO> voList = departments.stream()
                .map(d -> DepartmentVO.builder()
                        .id(d.getId())
                        .name(d.getName())
                        .description(d.getDescription())
                        .doctorsCount(d.getDoctorsCount())
                        .build())
                .collect(Collectors.toList());
        return Result.success("查询成功", voList);
    }

    @PostMapping("/create")
    @RequirePermission(RoleConstants.ADMIN)
    public Result<DepartmentVO> createDepartment(@RequestBody Department department) {
        departmentMapper.insert(department);
        DepartmentVO vo = DepartmentVO.builder()
                .id(department.getId())
                .name(department.getName())
                .description(department.getDescription())
                .doctorsCount(0)
                .build();
        return Result.success("创建成功", vo);
    }

    @PutMapping("/update")
    @RequirePermission(RoleConstants.ADMIN)
    public Result<Void> updateDepartment(@RequestBody Department department) {
        Department existing = departmentMapper.selectById(department.getId());
        if (existing == null) {
            return Result.error(404, "科室不存在");
        }
        departmentMapper.updateById(department);
        return Result.success("更新成功");
    }

    @DeleteMapping("/delete/{id}")
    @RequirePermission(RoleConstants.ADMIN)
    public Result<Void> deleteDepartment(@PathVariable Long id) {
        Department existing = departmentMapper.selectById(id);
        if (existing == null) {
            return Result.error(404, "科室不存在");
        }
        departmentMapper.deleteById(id);
        return Result.success("删除成功");
    }
}
