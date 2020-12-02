package com.lzb.tester.dto;

public enum ResultCode {
    SUCCESS(200,"success"),
    ERROR(500,"系统异常"),
    PARAMETER_ERR(400,"参数异常"),
    UN_AUTHORITY(403,"无权限")
    ;

    private Integer code;

    private String msg;

    ResultCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
