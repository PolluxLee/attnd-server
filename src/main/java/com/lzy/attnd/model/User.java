package com.lzy.attnd.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.*;


@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class User {


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name==null?"":name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpenid() {
        return openid==null?"":openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    //avoid null
    public Object getRemark() {
        return remark==null?new Object():remark;
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


    public String getStu_id() {
        return stu_id==null?"":stu_id;
    }

    public void setStu_id(String stu_id) {
        this.stu_id = stu_id;
    }

    public User() {
        this.name = "";
        this.openid = "";
        this.remark = new Object();
        this.stu_id = "";
    }


    public User(@Min(value = 1, groups = ID.class) @Min(0) int id, @NotBlank(groups = Name.class) String name, @NotBlank(groups = Openid.class) String openid, @NotNull(groups = Remark.class) Object remark, @Min(value = 0, groups = Status.class) int status, @NotBlank(groups = StuID.class) String stu_id) {
        this.id = id;
        this.name = name;
        this.openid = openid;
        this.remark = remark;
        this.status = status;
        this.stu_id = stu_id;
    }

    @Min(value = 1,groups = {ID.class,All.class})
    private int id;
    @NotBlank(groups = {Name.class,All.class})
    private String name;
    @NotBlank(groups = {Openid.class,All.class})
    private String openid;
    @NotNull(groups = {Remark.class,All.class})
    @JsonIgnore
    private Object remark;
    @Min(value = 0,groups = {Status.class,All.class})
    private int status;
    @NotBlank(groups = {StuID.class,All.class})
    @NotNull(groups = {StuIDNotNUll.class})
    private String stu_id;

    public interface ID {}
    public interface Name {}
    public interface Openid {}
    public interface Remark {}
    public interface Status {}
    public interface StuID {}
    public interface StuIDNotNUll {}

    public interface All {}


}
