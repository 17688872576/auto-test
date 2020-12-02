package com.lzb.tester.dto;

public class BaseResult<T> {

    private Integer code;
    private String msg;
    private T date;

    private BaseResult(Integer code, String msg, T date) {
        this.code = code;
        this.msg = msg;
        this.date = date;
    }

    private BaseResult() { }

    public static <T>BaseResult<T> success(String msg){
        return new BaseResult<T>(ResultCode.SUCCESS.getCode(),msg,null);
    }

    public static <T>BaseResult<T> success(String msg,T data){
        return new BaseResult<T>(ResultCode.SUCCESS.getCode(),msg,data);
    }

    public static <T>BaseResult<T> success(){
        return success(ResultCode.SUCCESS.getMsg());
    }

    public static <T>BaseResult<T> error(String msg){
        return new BaseResult<>(ResultCode.ERROR.getCode(),msg,null);
    }

    public static <T>BaseResult<T> error(){
        return error(ResultCode.ERROR.getMsg());
    }

    public static <T>BaseResult<T> parameterErr(String msg){
        return new BaseResult<>(ResultCode.PARAMETER_ERR.getCode(),msg,null);
    }

    public static <T>BaseResult<T> parameterErr(){
        return parameterErr(ResultCode.PARAMETER_ERR.getMsg());
    }

    public static <T>BaseResult<T> unAuthority(String msg){
        return new BaseResult<>(ResultCode.UN_AUTHORITY.getCode(),msg,null);
    }

    public static <T>BaseResult<T> unAuthority(){
        return parameterErr(ResultCode.UN_AUTHORITY.getMsg());
    }
}
