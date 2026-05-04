package com.example.usergenerator.dto.user;

import lombok.Data;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class AdminUserUpdateDTO {

    private Long id;

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

    @Pattern(regexp = "^(user|admin|doctor)$", message = "角色只能是user、admin或doctor")
    private String role;

    private Integer status;
}
