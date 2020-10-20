package com.clock.error;

public enum  BusinessError implements CommonError{
    //通用错误类型，1开头
    PARAMETER_VALIDATION_ERROR(10001, "参数不合法"),

    //未知错误，2开头
    UNKNOWN_ERROR(10002, "未知错误"),

    //用户信息相关错误定义，3开头
    USER_NOT_EXIST(30001, "用户不存在"),
    USER_LOGIN_FAIL(30002, "密码不正确"),
    USER_NOT_LOGIN(30003,"用户未登录"),
    USER_DUPLICATE_REGISTER(30004,"用户已注册")
    ;

    private int errorCode;
    private String errMessage;

    BusinessError(int errorCode, String errMessage) {
        this.errorCode = errorCode;
        this.errMessage = errMessage;
    }

    @Override
    public int getErrorCode() {
        return this.errorCode;
    }

    @Override
    public String getErrorMessage() {
        return this.errMessage;
    }

    @Override
    public CommonError setErrorMessage(String errorMessage) {
        this.errMessage = errorMessage;
        return this;
    }
}
