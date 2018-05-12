package com.lzy.attnd.utils;

import com.lzy.attnd.model.User;

import java.io.Serializable;

public class Session implements Serializable {
    private String name;
    private int status;
    private String openid;
    private String session_key;


    public Session(String name, int status, String openid, String session_key) {
        this.name = name;
        this.status = status;
        this.openid = openid;
        this.session_key = session_key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getSession_key() {
        return session_key;
    }

    public void setSession_key(String session_key) {
        this.session_key = session_key;
    }

    @Override
    public String toString(){
        return String.format("UserInfo:[name:%s,status:%d,openid:%s]::WxSessionKey:%s",name,status,openid,session_key);
    }
}
