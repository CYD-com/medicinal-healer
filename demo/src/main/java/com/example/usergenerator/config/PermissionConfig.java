package com.example.usergenerator.config;

import com.example.usergenerator.util.JwtUtil;
import com.example.usergenerator.util.PermissionUtil;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class PermissionConfig {

    private final JwtUtil jwtUtil;

    // 构造函数注入JwtUtil
    public PermissionConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // 初始化时将JwtUtil注入PermissionUtil
    @PostConstruct
    public void init() {
        PermissionUtil.setJwtUtil(jwtUtil);
    }
}