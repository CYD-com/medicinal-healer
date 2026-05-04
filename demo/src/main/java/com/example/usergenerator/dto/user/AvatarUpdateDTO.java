package com.example.usergenerator.dto.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AvatarUpdateDTO {

    @NotBlank(message = "头像地址不能为空")
    private String avatar;
}
