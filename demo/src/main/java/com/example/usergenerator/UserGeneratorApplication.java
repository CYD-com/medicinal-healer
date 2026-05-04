package com.example.usergenerator;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan("com.example.usergenerator.mapper")
@EnableAsync
public class UserGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserGeneratorApplication.class, args);
    }

}