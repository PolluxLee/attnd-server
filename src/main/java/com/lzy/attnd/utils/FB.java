package com.lzy.attnd.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lzy.attnd.constant.Code;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FB<T> {
    private int code;

    public FB(int code) {
        this.code = code;
        this.msg = "";
    }

    private String msg;
    private T data;

    public static FB SUCCESS(){
        return new FB(Code.GLOBAL_SUCCESS,"");
    }

    public static FB SUCCESS(Object data){
        return new FB<>(Code.GLOBAL_SUCCESS,"",data==null?new Object():data);
    }

    public static FB PARAM_INVALID(String msg){
        return new FB(Code.GLOBAL_PARAM_INVALID,msg==null?"":msg);
    }

    public static FB USER_NOT_EXIST(String msg){
        return new FB(Code.GLOBAL_USER_NOT_EXIST,msg==null?"":msg);
    }

    public static FB SYS_ERROR(String msg){
        return new FB(Code.GLOBAL_SYS_ERROR,msg==null?"":msg);
    }

    public static FB DB_FAILED(String msg){
        return new FB(Code.GLOBAL_DB_FAILED,msg==null?"":msg);
    }

    public FB(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public FB(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
}
