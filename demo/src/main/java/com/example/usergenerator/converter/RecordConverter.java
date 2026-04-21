package com.example.usergenerator.converter;

import com.example.usergenerator.dto.record.RecordCreateDTO;
import com.example.usergenerator.dto.record.RecordUpdateDTO;
import com.example.usergenerator.entity.Record;
import com.example.usergenerator.vo.record.RecordVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RecordConverter {

    RecordVO toVO(Record record);

    List<RecordVO> toVOList(List<Record> records);

    Record toEntity(RecordCreateDTO dto);

    void updateEntity(RecordUpdateDTO dto, @MappingTarget Record record);
}
