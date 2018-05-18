package com.lzy.attnd.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lzy.attnd.configure.ConfigBean;
import com.lzy.attnd.repository.UserRepository;
import com.lzy.attnd.service.WechatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
public class WechatServiceImpl implements WechatService {
    private final static Logger logger = LoggerFactory.getLogger(WechatServiceImpl.class);

    private final ConfigBean configBean;

    @Autowired
    public WechatServiceImpl(ConfigBean configBean) {
        this.configBean = configBean;
    }

    private RestTemplate rest = new RestTemplate();


    //TODO TEST
    public WxLoginFb Wx_Login(String code){
        ResponseEntity<String> response;
        try {
            String url = String.format("%s?appid=%s&secret=%s&js_code=%s&grant_type=%s",configBean.getWxlogin_url(),configBean.getAppid(),configBean.getAppsecret(),code,"authorization_code");
            logger.info("Wx_Login Request To Wx --- "+url);
            response = rest.getForEntity(url,String.class);
        } catch (RestClientException e) {
            logger.warn("LoginFb rest.getForObject failed: "+e.getMessage());
            return null;
        }


        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;
        try {
            root = mapper.readTree(response.getBody());
        } catch (IOException e) {
            logger.warn(String.format("LoginFb readTree failed: "+e.getMessage()));
            return null;
        }

        if (root.has("errcode")){
            logger.warn(String.format("LoginFb Wx response failed: Code=%d Msg=%s",root.get("errcode").asInt(),root.get("errmsg").asText()));
            return null;
        }

        if (!root.has("openid")||!root.has("session_key")){
            logger.warn("LoginFb Wx response invalid src="+root.toString());
            return null;
        }

        String openid =root.get("openid").asText();
        String session_key =root.get("session_key").asText();
        if (openid.equals("")||session_key.equals("")){
            logger.warn("LoginFb Wx response invalid openid or session_key empty src="+root.toString());
            return null;
        }


        return new WxLoginFb(openid,session_key);
    }
}
