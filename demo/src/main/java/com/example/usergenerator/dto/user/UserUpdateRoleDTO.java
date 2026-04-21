package com.example.usergenerator.dto.user;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class UserUpdateRoleDTO {

    @NotNull(message = "用户ID不能为空")
    private Long id;

    @NotBlank(message = "角色不能为空")
    @Pattern(regexp = "^(user|admin)$", message = "角色只能是user或admin")
    private String role;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
