package com.example.usergenerator.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.usergenerator.entity.Record;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

// @Repository 标记为持久层组件，让 Spring 扫描到并注入
@Repository
@Mapper
public interface RecordMapper extends BaseMapper<Record> {

    // 无需额外编写方法，BaseMapper 中已封装好以下核心删除方法：
    // 1. deleteById(Serializable id)：根据主键ID单个删除
    // 2. deleteBatchIds(Collection<? extends Serializable> idList)：根据主键ID集合批量删除
    // 3. delete(Wrapper<T> queryWrapper)：根据条件删除
}