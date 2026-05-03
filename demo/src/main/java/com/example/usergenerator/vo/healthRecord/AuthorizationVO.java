package com.example.usergenerator.vo.healthRecord;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AuthorizationVO {

    private List<AuthorizedDoctorVO> authorizedDoctors;

    private List<AuthorizedFamilyVO> authorizedFamily;

    @Data
    public static class AuthorizedDoctorVO {
        private Long authorizationId;
        private Long doctorId;
        private String doctorName;
        private String department;
        private LocalDateTime authorizedAt;
        private LocalDateTime expiresAt;
        private String status;
        private List<String> permissions;
    }

    @Data
    public static class AuthorizedFamilyVO {
        private Long authorizationId;
        private Long userId;
        private String realName;
        private String relation;
        private LocalDateTime authorizedAt;
        private List<String> permissions;
    }
}