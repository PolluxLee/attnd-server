package com.lzy.attnd.model;


import javax.validation.constraints.*;



public class User {


    public User(@Min(value = 1, groups = S.Query.class) @Min(0) int id, @NotBlank(groups = {C.Add.class, S.Ins.class, S.Query.class}) String name, @NotBlank(groups = {S.Ins.class, S.Query.class}) String openid, @NotNull(groups = {S.Ins.class, S.Query.class}) Object remark, @Min(value = 0, groups = S.Query.class) int status) {
        this.id = id;
        this.name = name;
        this.openid = openid;
        this.remark = remark;
        this.status = status;
    }

    public User(@Min(value = 1, groups = S.Query.class) @Min(0) int id, @NotBlank(groups = {C.Add.class, S.Ins.class, S.Query.class}) String name, @NotBlank(groups = {S.Ins.class, S.Query.class}) String openid) {
        this.id = id;
        this.name = name;
        this.openid = openid;
        this.remark = new Object();
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

    @Min(value = 1,groups = S.Query.class)
    @Min(0)
    private int id;
    @NotBlank(groups = {C.Add.class,S.Ins.class,S.Query.class})
    private String name;
    @NotBlank(groups = {S.Ins.class,S.Query.class})
    private String openid;
    @NotNull(groups = {S.Ins.class,S.Query.class})
    private Object remark;
    @Min(value = 0,groups = S.Query.class)
    private int status;

    public interface S {
        public interface Ins{}
        public interface Query{}
    }

    public interface C {
        public interface Add{}
    }

}
