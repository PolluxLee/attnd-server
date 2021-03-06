package com.lzy.attnd.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.lzy.attnd.constant.Code;
import com.lzy.attnd.exception.SysErrException;
import com.lzy.attnd.model.Attnd;
import com.lzy.attnd.model.Location;
import com.lzy.attnd.model.SignIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;

import java.util.Stack;

public class Utils {
    private final static Logger logger = LoggerFactory.getLogger(Utils.class);

    public static boolean chkCipherType(char c){
        if (c==Code.CIPHER_ATTND)
            return true;
        return false;
    }

    /**
     *
     * @param nowTime
     * @param attndStartTime
     * @param last
     * @return true -> end false -> ongoing
     */
    public static boolean chkAttndEnd(long nowTime,long attndStartTime,int last){
        if(nowTime<=0||attndStartTime<=0||last<0){
            throw new SysErrException("chkAttndEnd param invalid");
        }
        //last == 0 --> inf --> no end
        if (last!=0 && nowTime>attndStartTime+last*60*1000){
            return true;
        }

        return false;
    }



    //chk location , time
    public static int calSignInState(Attnd attnd,
           Location signInLocation,long signInTime,int signDistanceLimit,SignIn signIn
    ){
        if (attnd == null || signInLocation == null || signInTime < 0 || signDistanceLimit < 0 || signIn == null ||
                attnd.getLocation() == null || attnd.getStart_time()<0|| attnd.getLast()<0){
            String msg = "calSignInState param invalid";
            logger.error(msg);
            throw new SysErrException(msg);
        }

        int signin_status = Code.SIGNIN_OK;

        double dist = Location.calDistanceBetweenLocation(attnd.getLocation(),signInLocation);
        if (dist>signDistanceLimit){
            signin_status = Code.SIGNIN_LOCATION_BEYOND;
        }

        //end by creator or expired --> expired
        if (attnd.getStatus()==Code.ATTND_END || chkAttndEnd(signInTime,attnd.getStart_time(),attnd.getLast())){
            signin_status = Code.SIGNIN_EXPIRED;
        }

        signIn.setDistance(dist);
        return signin_status;
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

    /**
     * build cipher
     * @param type A
     * @param attndID
     * @param groupID
     * @return cipher
     */
    public static String CalCipher(char type,int attndID,int groupID){
        if (type!='A'||attndID<=0||groupID<=0){
            logger.error("CalCipher param invalid");
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(type);

        long nowTime = System.currentTimeMillis();
        String timeStr = LongToBase62LastK(nowTime,3);
        if (timeStr.equals("")) {
            logger.error("CalCipherB timeStr empty");
            return "";
        }
        sb.append(timeStr);

        String attndIDStr = LongToBase62LastK(attndID,10);
        if (attndIDStr.equals("")) {
            logger.error("attndIDStr empty");
            return "";
        }

        sb.append(attndIDStr);

        String groupIDStr = LongToBase62LastK(groupID,10);
        if (groupIDStr.equals("")) {
            logger.error("groupIDStr empty");
            return "";
        }

        sb.append(groupIDStr);

        return sb.toString();
    }


    /**
     * build cipher
     * @param type N/G/S
     * @param tail_id the id in the tail to identify
     * @return cipher
     */
    public static String CalCipher(char type,int tail_id){
        if (!chkCipherType(type)||tail_id<=0){
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


        String idStr = LongToBase62LastK(tail_id,10);
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

    //I -> =
    //l -> +
    private static String digths = "0123456789abcdefghijk+mnopqrstuvwxyzABCDEFGH=JKLMNOPQRSTUVWXYZ";


    public static int ChkIDBase62Length(int ID,int max){
        if (ID<=0){
            return -1;
        }
        long flag = 62 ;
        int len = 1;
        while (ID>=flag){
            flag=flag*62;
            len++;
            if (len>=max)
                return max;
        }
        return len;
    }

    //后k位
    public static String LongToBase62LastK(long id,int lastK) {
        if (id<=0||lastK<=0)
            return "";
        StringBuffer str = new StringBuffer();
        long num = id;
        while (num != 0) {
            if (str.length()==lastK)
                break;
            str.insert(0,digths.charAt((int) (num % 62)));
            num /= 62;
        }
        return str.toString();
    }

    public static long Base62LastKToLong(String cipher,int lastK){
        if (cipher==null || cipher.equals("")||lastK<=0)
            return -1;
        long ans = 0;
        int index = cipher.length()-lastK;
        while (index<cipher.length()){
            ans = ans*62 + digths.indexOf(cipher.charAt(index));
            index++;
        }
        return ans;
    }

}
