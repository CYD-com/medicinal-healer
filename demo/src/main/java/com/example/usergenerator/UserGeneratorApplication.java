package com.example.usergenerator;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.usergenerator.mapper")
public class UserGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserGeneratorApplication.class, args);
    }

}