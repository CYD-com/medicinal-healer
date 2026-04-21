package com.example.usergenerator.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.usergenerator.common.ResultCode;
import com.example.usergenerator.constant.RecordConstants;
import com.example.usergenerator.converter.RecordConverter;
import com.example.usergenerator.dto.record.RecordCreateDTO;
import com.example.usergenerator.dto.record.RecordQueryDTO;
import com.example.usergenerator.dto.record.RecordUpdateDTO;
import com.example.usergenerator.entity.Record;
import com.example.usergenerator.exception.BusinessException;
import com.example.usergenerator.mapper.RecordMapper;
import com.example.usergenerator.service.RecordService;
import com.example.usergenerator.vo.record.RecordVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecordServiceImpl extends ServiceImpl<RecordMapper, Record> implements RecordService {

    private final RecordConverter recordConverter;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RecordVO createRecord(RecordCreateDTO dto) {
        Record record = recordConverter.toEntity(dto);
        this.save(record);
        log.info("创建记录成功，ID：{}", record.getId());
        return recordConverter.toVO(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRecord(RecordUpdateDTO dto) {
        Record record = this.getById(dto.getId());
        if (record == null) {
            throw new BusinessException(ResultCode.RECORD_NOT_FOUND);
        }

        recordConverter.updateEntity(dto, record);
        this.updateById(record);
        log.info("更新记录成功，ID：{}", dto.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRecord(Long id) {
        Record record = this.getById(id);
        if (record == null) {
            throw new BusinessException(ResultCode.RECORD_NOT_FOUND);
        }

        this.removeById(id);
        log.info("删除记录成功，ID：{}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteRecords(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "ID列表不能为空");
        }

        this.removeByIds(ids);
        log.info("批量删除记录成功，数量：{}", ids.size());
    }

    @Override
    public IPage<RecordVO> pageRecords(RecordQueryDTO dto) {
        LambdaQueryWrapper<Record> wrapper = buildQueryWrapper(dto);
        Page<Record> page = new Page<>(dto.getPage(), dto.getSize());
        IPage<Record> recordPage = this.page(page, wrapper);

        Page<RecordVO> voPage = new Page<>(recordPage.getCurrent(), recordPage.getSize(), recordPage.getTotal());
        voPage.setRecords(recordConverter.toVOList(recordPage.getRecords()));

        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initTestData() {
        List<Record> records = new ArrayList<>();
        String[] categories = {"餐饮", "交通", "购物", "娱乐", "医疗"};
        String[] types = {RecordConstants.TYPE_EXPENSE, RecordConstants.TYPE_INCOME};
        String[] statuses = {RecordConstants.STATUS_COMPLETED, RecordConstants.STATUS_PENDING, RecordConstants.STATUS_CANCELLED};

        for (int i = 0; i < 15; i++) {
            Record record = new Record();
            record.setCategory(categories[RandomUtil.randomInt(categories.length)]);
            record.setType(types[RandomUtil.randomInt(types.length)]);
            record.setStatus(statuses[RandomUtil.randomInt(statuses.length)]);
            record.setAmount(new BigDecimal(RandomUtil.randomInt(10, 1000)));
            record.setTime(LocalDate.now().minusDays(RandomUtil.randomInt(0, 30)));
            records.add(record);
        }

        this.saveBatch(records);
        log.info("初始化测试数据成功，数量：{}", records.size());
    }

    private LambdaQueryWrapper<Record> buildQueryWrapper(RecordQueryDTO dto) {
        LambdaQueryWrapper<Record> wrapper = new LambdaQueryWrapper<>();

        if (dto.getType() != null && !dto.getType().isEmpty()) {
            wrapper.eq(Record::getType, dto.getType());
        }
        if (dto.getCategory() != null && !dto.getCategory().isEmpty()) {
            wrapper.eq(Record::getCategory, dto.getCategory());
        }
        if (dto.getStatus() != null && !dto.getStatus().isEmpty()) {
            wrapper.eq(Record::getStatus, dto.getStatus());
        }
        if (dto.getStartTime() != null) {
            wrapper.ge(Record::getTime, dto.getStartTime());
        }
        if (dto.getEndTime() != null) {
            wrapper.le(Record::getTime, dto.getEndTime());
        }

        wrapper.orderByDesc(Record::getTime);
        return wrapper;
    }
}
