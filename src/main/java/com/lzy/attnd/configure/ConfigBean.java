package com.lzy.attnd.configure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "com.lzy")
public class ConfigBean {

    public final String getSession_key() {
        return session_key;
    }

    public void setSession_key(String session_key) {
        this.session_key = session_key;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getAppsecret() {
        return appsecret;
    }

    public void setAppsecret(String appsecret) {
        this.appsecret = appsecret;
    }

    public String getWxlogin_url() {
        return wxlogin_url;
    }

    public void setWxlogin_url(String wxlogin_url) {
        this.wxlogin_url = wxlogin_url;
    }

    public int getMeter_limit() {
        return meter_limit;
    }

    public void setMeter_limit(int meter_limit) {
        this.meter_limit = meter_limit;
    }

    private String session_key;
    private String appid;
    private String appsecret;
    private String wxlogin_url;
    private int meter_limit;


}
