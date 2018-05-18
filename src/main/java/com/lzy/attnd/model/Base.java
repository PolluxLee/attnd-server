package com.lzy.attnd.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.lzy.attnd.repository.UserRepository;
import com.lzy.attnd.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class Base {

    public Base(@NotNull(groups = {Remark.class, BaseAll.class}) Object remark, @Min(value = 0, groups = {Status.class, BaseAll.class}) int status) {
        this.remark = remark;
        this.status = status;
    }

    public Object getRemark() {
        return remark;
    }

    @JsonIgnore
    @Nullable
    public String getRemarkJson(){
        return Utils.ObjectToJson(remark);
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

    public Base() {
    }

    @NotNull(groups = {Base.Remark.class,Base.BaseAll.class})
    @JsonIgnore
    protected Object remark;
    @Min(value = 0,groups = {Base.Status.class,Base.BaseAll.class})
    protected int status;


    public interface Remark {}
    public interface Status {}

    public interface BaseAll {}

}
