package com.lzy.attnd.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.lzy.attnd.constant.Code;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;

import java.util.Stack;

public class Utils {
    private final static Logger logger = LoggerFactory.getLogger(Utils.class);

    public static boolean chkCipherType(char c){
        if (c==Code.CIPHER_ATTND||c==Code.CIPHER_ENTRY||c==Code.CIPHER_SINGLE||c==Code.CIPHER_NOGROUP)
            return true;
        return false;
    }

    public static char GetTypeViaStatus(int attnd_status){
        if (attnd_status<=0){
            return Code.CIPHER_ATTND;
        }
        switch (attnd_status){
            case Code.ATTND_NORMAL:
                return Code.CIPHER_ATTND;
            case Code.ATTND_ENTRY:
                return Code.CIPHER_ENTRY;
            case Code.ATTND_NOGROUP:
                return Code.CIPHER_NOGROUP;
        }
        return Code.CIPHER_ATTND;
    }


    public static String CalCipher(char type,int attnd_id){
        if (!chkCipherType(type)||attnd_id<=0){
            logger.error("CalCipher param invalid");
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(type);
        long nowTime = System.currentTimeMillis();
        String timeStr = LongToBase62LastK(nowTime,3);
        if (timeStr.equals("")) {
            logger.error("timeStr empty");
            return "";
        }
        sb.append(timeStr);


        String idStr = LongToBase62LastK(attnd_id,10);
        if (idStr.equals("")) {
            logger.error("idStr empty");
            return "";
        }

        sb.append(idStr);

        return sb.toString();
    }


    @Nullable
    public static String ObjectToJson(Object object){
        if (object==null)
            return null;

        ObjectMapper mapper = new ObjectMapper();
        //test will failed if not this sentence
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        String jsonStr;
        try {
            jsonStr = mapper.writeValueAsString(object);
        }catch (JsonProcessingException jpe){
            logger.error("object to json failed: "+jpe.getMessage());
            jpe.printStackTrace();
            return null;
        }
        return jsonStr;
    }


    private static String digths = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";


    //后k位
    public static String LongToBase62LastK(long id,int lastK) {
        if (id<=0||lastK<=0)
            return "";
        StringBuffer str = new StringBuffer();
        long num = id;
        while (num != 0) {
            if (str.length()==lastK)
                break;
            str.append(digths.charAt((int) (num % 62)));
            num /= 62;
        }
        return str.toString();
    }

}
