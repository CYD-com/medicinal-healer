package com.example.usergenerator.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("operation_log")
public class OperationLog {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String username;

    private String role;

    private String operationType;

    private String targetType;

    private Long targetId;

    private String content;

    private String ipAddress;

    private String userAgent;

    private Integer status;

    private String errorMsg;

    private LocalDateTime createTime;
}
