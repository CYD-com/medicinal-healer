package com.example.usergenerator.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.usergenerator.entity.OperationLog;

import java.util.List;
import java.util.Map;

public interface OperationLogService extends IService<OperationLog> {

    void recordLog(Long userId, String username, String role, String operationType,
                   String targetType, Long targetId, String content,
                   String ipAddress, String userAgent, String status, String errorMsg);

    Map<String, Object> getLogs(String operationType, String targetType, Long userId, Integer page, Integer size);
}
