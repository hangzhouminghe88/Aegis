package com.mingheinfo.common.utils;

import com.mingheinfo.common.json.ApiJson;

public class ResultUtil {

    private ApiJson result;

    public ResultUtil(){
        result=new ApiJson();
        result.setSuccess(true);
        result.setCode("200");
    }

    public ApiJson setData(Object t){
        this.result.setResult(t);
        this.result.setCode("200");
        return this.result;
    }

    public ApiJson setSuccessMsg(String msg){
        this.result.setSuccess(true);
        this.result.setMsg(msg);
        this.result.setCode("200");
        this.result.setResult(null);
        return this.result;
    }

    public ApiJson setData(Object t, String msg){
        this.result.setResult(t);
        this.result.setCode("200");
        this.result.setMsg(msg);
        return this.result;
    }

    public ApiJson setErrorMsg(String msg){
        this.result.setSuccess(false);
        this.result.setMsg(msg);
        this.result.setCode("500");
        return this.result;
    }

    public ApiJson setErrorMsg(String code, String msg){
        this.result.setSuccess(false);
        this.result.setMsg(msg);
        this.result.setCode(code);
        return this.result;
    }

    public static ApiJson data(Object t){

        return new ResultUtil().setData(t);
    }

    public static ApiJson data(Object t, String msg){
        return new ResultUtil().setData(t, msg);
    }

    public static ApiJson success(String msg){
        return new ResultUtil().setSuccessMsg(msg);
    }

    public static ApiJson success(){
        return new ResultUtil().result;
    }    
    
    public static ApiJson error(String msg){
        return new ResultUtil().setErrorMsg(msg);
    }

    public static ApiJson error(String code, String msg){
        return new ResultUtil().setErrorMsg(code, msg);
    }
}
