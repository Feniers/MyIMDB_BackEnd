package com.example.myimdb.domain;

public enum ResultStatus {
    SUCCESS(200, "成功"),
    ERROR(500, "未定义错误"),
    BAD_REQUEST(400, "请求错误"),
    USERNAME_OR_PASSWORD_ERROR(-1001, "用户名或密码错误"),
    USER_NOT_FOUND(-1001, "用户不存在"),
    USER_NOT_LOGIN(-1001, "用户未登录"),

    USER_ALREADY_LOGIN(-1001, "用户已登录"),
    RESOURCE_NOT_FOUND(-1004, "资源不存在"),
    AUTHORIZATION_HEADER_NOT_FOUND(-1005, "未找到Authorization头"),
    PERMISSION_DENIED(-1006, "权限不够"),
    USERNAME_EXIST(-1007, "用户名已存在");


    /**
     * 返回码
     */
    private int code;

    /**
     * 返回结果描述
     */
    private String message;

    ResultStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
