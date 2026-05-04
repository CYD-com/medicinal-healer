package com.example.usergenerator.dto.user;

import lombok.Data;

@Data
public class UpdateProfileDTO {

    private String realName;

    private String gender;

    private Integer age;

    private String phone;

    private String email;

    private String address;

    private String avatar;
}
