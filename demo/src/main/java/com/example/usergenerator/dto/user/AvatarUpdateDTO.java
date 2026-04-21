package com.example.usergenerator.dto.user;

import javax.validation.constraints.NotBlank;

public class AvatarUpdateDTO {

    @NotBlank(message = "头像地址不能为空")
    private String avatar;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
