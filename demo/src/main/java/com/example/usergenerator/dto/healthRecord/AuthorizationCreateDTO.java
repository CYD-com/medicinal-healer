package com.example.usergenerator.dto.healthRecord;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AuthorizationCreateDTO {

    private String targetType;

    private Long targetId;

    private List<String> permissions;

    private LocalDateTime expiresAt;
}