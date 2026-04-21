package com.example.usergenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

// 👇 重点：exclude 中明确排除 SecurityAutoConfiguration，注意包路径正确
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
//@MapperScan("com.example.usergenerator.mapper")
public class UserGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserGeneratorApplication.class, args);
    }

}