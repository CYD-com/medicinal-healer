package com.example.usergenerator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.usergenerator.entity.OperationLog;
import com.example.usergenerator.mapper.OperationLogMapper;
import com.example.usergenerator.service.OperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {

    @Override
    @Async
    public void recordLog(Long userId, String username, String role, String operationType,
                          String targetType, Long targetId, String content,
                          String ipAddress, String userAgent, String status, String errorMsg) {
        try {
            OperationLog operationLog = new OperationLog();
            operationLog.setUserId(userId);
            operationLog.setUsername(username);
            operationLog.setRole(role);
            operationLog.setOperationType(operationType);
            operationLog.setTargetType(targetType);
            operationLog.setTargetId(targetId);
            operationLog.setContent(content);
            operationLog.setIpAddress(ipAddress);
            operationLog.setUserAgent(userAgent);
            operationLog.setStatus(status != null ? Integer.parseInt(status) : 1);
            operationLog.setErrorMsg(errorMsg);
            operationLog.setCreateTime(LocalDateTime.now());
            this.save(operationLog);
        } catch (Exception e) {
            log.error("记录操作日志失败", e);
        }
    }

    @Override
    public Map<String, Object> getLogs(String operationType, String targetType, Long userId, Integer page, Integer size) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        if (operationType != null && !operationType.isEmpty()) {
            wrapper.eq(OperationLog::getOperationType, operationType);
        }
        if (targetType != null && !targetType.isEmpty()) {
            wrapper.eq(OperationLog::getTargetType, targetType);
        }
        if (userId != null) {
            wrapper.eq(OperationLog::getUserId, userId);
        }
        wrapper.orderByDesc(OperationLog::getCreateTime);

        List<OperationLog> allLogs = this.list(wrapper);
        int total = allLogs.size();
        int from = (page - 1) * size;
        int to = Math.min(from + size, total);
        List<OperationLog> logs = from < total ? allLogs.subList(from, to) : Collections.emptyList();

        Map<String, Object> result = new HashMap<>();
        result.put("logs", logs);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return result;
    }
}
