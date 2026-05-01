package com.example.usergenerator.common;

public enum ResultCode {

    SUCCESS(200, "操作成功"),

    ERROR(500, "操作失败"),

    BAD_REQUEST(400, "请求参数错误"),

    UNAUTHORIZED(401, "未授权，请先登录"),

    FORBIDDEN(403, "权限不足"),

    NOT_FOUND(404, "资源不存在"),

    USERNAME_ALREADY_EXISTS(1001, "用户名已存在"),

    USERNAME_OR_PASSWORD_ERROR(1002, "用户名或密码错误"),

    USER_NOT_FOUND(1003, "用户不存在"),

    USER_ALREADY_EXISTS(1004, "用户已存在"),

    INVALID_CREDENTIALS(1005, "用户名或密码错误"),

    INVALID_CAPTCHA(1006, "验证码错误或已过期"),

    DRUG_NOT_FOUND(1007, "药品不存在"),

    PRESCRIPTION_NOT_FOUND(1008, "处方不存在"),

    RECORD_NOT_FOUND(2001, "记录不存在"),

    DUPLICATE_REQUEST(3001, "请勿重复提交"),

    INVALID_TOKEN(401, "无效的token"),

    SYSTEM_ERROR(5000, "系统异常");

    private final Integer code;

    private final String msg;

    ResultCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
