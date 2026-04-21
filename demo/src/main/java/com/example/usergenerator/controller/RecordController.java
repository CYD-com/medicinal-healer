package com.example.usergenerator.controller;

import com.example.usergenerator.annotation.RequirePermission;
import com.example.usergenerator.annotation.RepeatSubmit;
import com.example.usergenerator.common.Result;
import com.example.usergenerator.constant.RoleConstants;
import com.example.usergenerator.dto.record.RecordCreateDTO;
import com.example.usergenerator.dto.record.RecordQueryDTO;
import com.example.usergenerator.dto.record.RecordUpdateDTO;
import com.example.usergenerator.service.RecordService;
import com.example.usergenerator.vo.record.RecordVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/record")
@RequiredArgsConstructor
@Validated
public class RecordController {

    private final RecordService recordService;

    @PostMapping("/add")
    @RequirePermission({RoleConstants.USER, RoleConstants.ADMIN})
    @RepeatSubmit(timeout = 3000)
    public Result<RecordVO> addRecord(@Valid @RequestBody RecordCreateDTO dto) {
        RecordVO vo = recordService.createRecord(dto);
        return Result.success("添加成功", vo);
    }

    @PutMapping("/update/{id}")
    @RequirePermission({RoleConstants.USER, RoleConstants.ADMIN})
    @RepeatSubmit(timeout = 3000)
    public Result<Void> updateRecord(@PathVariable Long id,
                                      @Valid @RequestBody RecordUpdateDTO dto) {
        if (id == null) {
            return Result.error(400, "ID不能为空");
        }
        dto.setId(id);
        recordService.updateRecord(dto);
        return Result.success("更新成功");
    }

    @DeleteMapping("/delete/{id}")
    @RequirePermission({RoleConstants.USER, RoleConstants.ADMIN})
    public Result<Void> deleteRecord(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        recordService.deleteRecord(id);
        return Result.success("删除成功");
    }

    @DeleteMapping("/delete/batch")
    @RequirePermission({RoleConstants.USER, RoleConstants.ADMIN})
    public Result<Void> deleteRecordBatch(@RequestBody List<Long> ids) {
        recordService.batchDeleteRecords(ids);
        return Result.success("批量删除成功");
    }

    @GetMapping("/list")
    @RequirePermission({RoleConstants.USER, RoleConstants.ADMIN})
    public Result<IPage<RecordVO>> listRecords(@Valid @ModelAttribute RecordQueryDTO dto) {
        IPage<RecordVO> page = recordService.pageRecords(dto);
        return Result.success("查询成功", page);
    }

    @GetMapping("/init")
    @RequirePermission({RoleConstants.USER, RoleConstants.ADMIN})
    public Result<Void> initData() {
        recordService.initTestData();
        return Result.success("初始化成功");
    }
}
