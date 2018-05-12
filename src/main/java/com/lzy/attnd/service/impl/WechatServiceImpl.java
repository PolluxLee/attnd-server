package com.lzy.attnd.service.impl;

import com.lzy.attnd.configure.ConfigBean;
import com.lzy.attnd.repository.UserRepository;
import com.lzy.attnd.service.WechatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
public class WechatServiceImpl implements WechatService {
    private final static Logger logger = LoggerFactory.getLogger(UserRepository.class);

    @Autowired
    ConfigBean configBean;


    public WxLoginFb Wx_Login(String code){
        System.out.println(configBean.getSession_key());
        return new WxLoginFb("testoid","testsessionkey");
        //TODO 微信待接入
        /*RestTemplate rest = new RestTemplate();
        LoginFb loginFb = null;
        try {
            loginFb = rest.getForObject(String.format("%s?appid=%s&secret=%s&js_code=%s&grant_type=%s",configBean.getWxlogin_url(),configBean.getAppid(),configBean.getAppsecret(),code,"authorization_code"),LoginFb.class);
        } catch (RestClientException e) {
            logger.warn("LoginFb rest.getForObject failed: "+e.getMessage());
            return null;
        }
        return loginFb;*/
    }
}
