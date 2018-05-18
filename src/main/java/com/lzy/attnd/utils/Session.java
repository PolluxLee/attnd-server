package com.lzy.attnd.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;
import java.io.Serializable;

public class Session implements Serializable {
    private final static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public static Session GetAttribute(HttpSession httpSession,String session_key){
        if (httpSession == null || session_key.equals(""))
            return null;
        Session sess;
        try {
            sess = ((Session) httpSession.getAttribute(session_key));
        } catch (ClassCastException cce) {
            logger.error("GetAttribute cast failed: "+cce.getMessage());
            return null;
        }
        return sess;
    }

    public Session(int userID, String name, int status, String openid, String session_key) {
        UserID = userID;
        this.name = name;
        this.status = status;
        this.openid = openid;
        this.session_key = session_key;
    }

    public static Logger getLogger() {
        return logger;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
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

    private int UserID;
    private String name;
    private int status;
    private String openid;
    private String session_key;




    @Override
    public String toString(){
        return String.format("UserInfo:[UserID:%s,name:%s,status:%d,openid:%s]::WxSessionKey:%s",UserID,name,status,openid,session_key);
    }
}
