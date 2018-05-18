package com.lzy.attnd.service;

import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Validated
public interface WechatService {

    public static class WxLoginErr {
        public WxLoginErr(int errcode, String errmsg) {
            this.errcode = errcode;
            this.errmsg = errmsg;
        }

        public int getErrcode() {
            return errcode;
        }

        public String getErrmsg() {
            return errmsg;
        }

        private int errcode;
        private String errmsg;
    }

    public static class WxLoginFb {
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

        public WxLoginFb(String openid, String session_key) {
            this.openid = openid;
            this.session_key = session_key;
        }

        private String openid;
        private String session_key;
    }


    @Nullable
    WxLoginFb Wx_Login(@NotBlank String code);
}
