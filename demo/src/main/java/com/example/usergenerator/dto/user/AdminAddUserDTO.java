package com.example.usergenerator.dto.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class AdminAddUserDTO {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20之间")
    private String password;

    @Pattern(regexp = "^(user|admin|doctor)$", message = "角色只能是user、admin或doctor")
    private String role;

    @Size(max = 50, message = "真实姓名长度不能超过50")
    private String realName;

    private String gender;

    private Integer age;

    @Size(max = 20, message = "手机号长度不能超过20")
    private String phone;

    @Size(max = 100, message = "邮箱长度不能超过100")
    private String email;

    @Size(max = 20, message = "身份证号长度不能超过20")
    private String idCard;

    @Size(max = 200, message = "地址长度不能超过200")
    private String address;

    private Integer status;
}
