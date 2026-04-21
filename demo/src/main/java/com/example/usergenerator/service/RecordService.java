package com.example.usergenerator.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.usergenerator.dto.record.RecordCreateDTO;
import com.example.usergenerator.dto.record.RecordQueryDTO;
import com.example.usergenerator.dto.record.RecordUpdateDTO;
import com.example.usergenerator.entity.Record;
import com.example.usergenerator.vo.record.RecordVO;

import java.util.List;

public interface RecordService extends IService<Record> {

    RecordVO createRecord(RecordCreateDTO dto);

    void updateRecord(RecordUpdateDTO dto);

    void deleteRecord(Long id);

    void batchDeleteRecords(List<Long> ids);

    IPage<RecordVO> pageRecords(RecordQueryDTO dto);

    void initTestData();
}
