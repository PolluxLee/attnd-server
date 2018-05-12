package com.lzy.attnd.model;



import javax.validation.constraints.*;

public class User {
    public User(@Min(0) int id, @NotBlank String name, @NotNull String openid, @NotNull Object remark, @Min(0) int status) {
        this.id = id;
        this.name = name;
        this.openid = openid;
        this.remark = remark;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public Object getRemark() {
        return remark;
    }

    public void setRemark(Object remark) {
        this.remark = remark;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public User(@Min(0) int id, @NotBlank String name, @NotNull String openid) {
        this.id = id;
        this.name = name;
        this.openid = openid;
        this.remark = new Object();
    }

    @Min(0)
    private int id;
    @NotBlank
    private String name;
    @NotNull
    private String openid;
    @NotNull
    private Object remark;
    @Min(0)
    private int status;



}
