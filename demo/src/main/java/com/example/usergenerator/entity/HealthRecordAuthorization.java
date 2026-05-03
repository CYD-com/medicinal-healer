package com.example.usergenerator.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("health_record_authorization")
public class HealthRecordAuthorization {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String targetType;

    private Long targetId;

    private String permissions;

    private LocalDateTime expiresAt;

    private String status;

    private LocalDateTime authorizedAt;
}