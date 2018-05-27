package com.lzy.attnd.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lzy.attnd.configure.ConfigBean;
import com.lzy.attnd.constant.Code;
import com.lzy.attnd.model.*;
import com.lzy.attnd.service.AttndService;
import com.lzy.attnd.service.SignInService;
import com.lzy.attnd.service.UserService;
import com.lzy.attnd.utils.FB;
import com.lzy.attnd.utils.Session;
import com.lzy.attnd.utils.Utils;
import org.hibernate.validator.constraints.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashMap;

@RestController
@Validated
public class AttndController {

    private final static Logger logger = LoggerFactory.getLogger(AttndController.class);


    private final AttndService attndService;
    private final UserService userService;
    private final SignInService signInService;

    private final ConfigBean configBean;

    @Autowired
    public AttndController(AttndService attndService,UserService userService, SignInService signInService, ConfigBean configBean) {
        this.attndService = attndService;
        this.userService = userService;
        this.signInService = signInService;
        this.configBean = configBean;
    }

    /**
     * @api {post} /api/attnd addAttnd
     * @apiName addAttnd
     * @apiGroup Attnd
     *
     * @apiParam {String{0..50}} attnd_name 考勤名称
     * @apiParam {String{0..50}} addr_name location name
     * @apiParam {Number{0-}} start_time need check start_time + last > now milliseconds
     * @apiParam {Number{0-1440}} [last] attendance last time unit->minutes
     * @apiParam {Number{-90-90}} latitude float
     * @apiParam {Number{-180-180}} longitude float
     * @apiParam {Number{0-}} accuracy float
     * @apiParam {String{0..50}} addr_name location name
     * @apiParam {String{0..50}} teacher_name if user exist -> do nothing
     * @apiParamExample {json} Req-create:
     * {"attnd_name":"操作系统","start_time":1526826235000,"last":20,"location":{"latitude":35.4,"longitude":174.4,"accuracy":30.0},"addr_name":"外环西路","teacher_name":"wjx"}
     *
     * @apiSuccess {String} cipher 口令 标识位(标识录入/考勤)+通过62进制时间戳后3位 + attnd_id 的62进制表示
     * @apiSuccessExample {json} Resp:
     * {"code":1000,"msg":"","data":{"attnd_id":5415,"cipher":"G548QC","userinfo":{"id":1,"openid":"fdsafe51515","name":"lzp"}}}
     */
    /***/
    @PostMapping("/attnd")
    public FB addAttnd(
            HttpSession httpSession,
            @RequestAttribute("attnd") Session session,
            @Validated({Attnd.Name.class,Attnd.StartTime.class,Attnd.Location_Struct.class,Attnd.AddrName.class,
                    Attnd.TeacherName.class})
                                 @RequestBody Attnd attnd){

        //chk user exist get Teacherid
        User user = userService.FindUserByOpenid(session.getOpenid());
        if (user==null){
            user = new User();
            user.setOpenid(session.getOpenid());
            user.setName(attnd.getTeacher_name());
            user.setRemark(new Object());
            user.setStu_id("");
            int id = userService.InsIgnoreUserInfo(user);
            if (id==0){
                return FB.DB_FAILED("addAttnd InsIgnoreUserInfo failed");
            }
            user.setId(id);
        }
        attnd.setTeacher_name(user.getName());
        session.setUserID(user.getId());
        session.setName(attnd.getTeacher_name());

        //if user info has updated -> update session
        httpSession.setAttribute(configBean.getSession_key(),session);

        attnd.setStatus(Code.ATTND_NORMAL);

        //add attnd
        attnd.setRemark(new Object());
        attnd.setTeacher_id(session.getUserID());
        String cipher = attndService.AddAttnd(attnd);
        if (cipher==null||cipher.equals("")){
            return FB.DB_FAILED("addAttnd cipher invalid");
        }

        if (attnd.getAttnd_id()<=0){
            return FB.DB_FAILED("addAttnd AddAttnd no id return");
        }

        HashMap<String,Object> fbJson = new HashMap<>();
        fbJson.put("attnd_id",attnd.getAttnd_id());
        fbJson.put("cipher",cipher);
        fbJson.put("userinfo",user);
        return FB.SUCCESS(fbJson);
    }



    /**
     * @api {get} /api/attnd chkAttnd
     * @apiName chkAttnd
     * @apiGroup Attnd
     *
     * @apiParamExample {String} Req:
     * cipher=AZXQAS
     *
     * @apiSuccess {Number} status 1-->NORMAL 4-->DEL 5 -> end by creator
     * @apiSuccessExample {json} Resp:
     * {"code":1000,"msg":"","data":{"teacher_id":1,"attnd_id":123,"cipher":"GZXQAS","status":1,"attnd_name":"操作系统","start_time":15577418,"last":20,"location":{"latitude":35.4,"longitude":174.4,"accuracy":30.0},"addr_name":"外环西路","teacher_name":"wjx"}}
     *
     *
     * @apiError (Error-Code) 3001 attnd not exist
     */
    /****/
    @GetMapping("/attnd")
    public FB chkAttnd(
            @NotBlank @Size(max = 50) @RequestParam("cipher") String cipher
    ){
        Attnd attnd = attndService.ChkAttnd(cipher);
        if (attnd==null){
            return new FB(Code.ATTND_NOT_EXIST);
        }

        return FB.SUCCESS(attnd);
    }

    /**
     * @api {get} /api/attnd/hisname chkAttnd_hisname
     * @apiName chkAttnd_hisname
     * @apiGroup Attnd
     * @apiDescription get latest top 15 distinct
     *
     *
     * @apiSuccessExample {json} Resp:
     * {"code":1000,"msg":"","data":["操作系统","计算机网络"]}
     */
    /***/
    @GetMapping("/attnd/hisname")
    public FB chkAttnd_hisName(
            @RequestAttribute("attnd") Session session
    ){
        String[] hisName = attndService.ChkHisAttndName(session.getUserID(),15);
        if (hisName==null || hisName.length<=0){
            String msg = "ChkHisAttndName hisName empty or null";
            logger.warn(msg);
            return FB.SYS_ERROR(msg);
        }
        return FB.SUCCESS(hisName);
    }

    /**
     * @api {get} /api/attnd/hisaddr chkAttnd_hisAddr
     * @apiName chkAttnd_hisAddr
     * @apiGroup Attnd
     * @apiDescription get latest top 15 distinct
     *
     * @apiSuccessExample {json} Resp:
     * {"code":1000,"msg":"","data":["理科南305","文新楼317"]}
     */
    /***/
    @GetMapping("/attnd/hisaddr")
    public FB chkAttndHisAddr(
            @RequestAttribute("attnd") Session session
    ){
        String[] hisName = attndService.ChkHisAttndAddr(session.getUserID(),15);
        if (hisName==null || hisName.length<=0){
            String msg = "chkAttndHisAddr hisName empty or null";
            logger.warn(msg);
            return FB.SYS_ERROR(msg);
        }
        return FB.SUCCESS(hisName);
    }


    /**
     * @apiDefine Pagination
     * @apiParam {Number{1..}} page  分页页号 1开始
     * @apiParam {Number{1..}} page_size  每页长度
     */

    /**
     * @apiDefine Search
     * @apiParam {String} query 搜索关键字(考勤名称)
     */


    /**
     * @api {get} /api/attndlist chkAttndlist
     * @apiName chkAttndlist
     * @apiGroup Attnd
     *
     * @apiUse Pagination
     * @apiUse Search
     * @apiParam list_type {Number=1,2} 查看的是 1->我创建的考勤/2->我的签到
     *
     * @apiSuccessExample {json} Resp:
     * {"code":1000,"msg":"","data":{"attnds":[{"attnd_id":1,"attnd_name":"操作系统1","start_time":1522512000,"last":20,"location":{"latitude":23.4,"longitude":174.4,"accuracy":30.0},"addr_name":"外环西路","teacher_name":"lzy","teacher_id":1,"cipher":"Gwvk1"}],"count":1}}
     *
     */
    /***/
    @GetMapping("/attndlist")
    public FB chkAttndList(
            @RequestAttribute("attnd") Session session,
            @Min(0) @RequestAttribute("start") int start,
            @Min(1) @RequestAttribute("rows") int rows,
            @NotNull @Size(max = 50)@RequestParam("query") String query,
            @Range(min = 1,max = 2) @RequestParam("list_type") int listType
    ){
        PaginationAttnd attndsPage = null;

        //ORDER BY CREATEDAT ASC
        if (listType==1){
            attndsPage = attndService.ChkAttndListByUser(session.getUserID(),start,rows,query);
        }

        if (listType == 2){
            attndsPage = attndService.ChkAttndList_SigninByUser(session.getOpenid(),start,rows,query);
        }

        if (attndsPage==null){
            String msg = "chkAttndList unreachable branch in attndspage null";
            logger.error(msg);
            return FB.SYS_ERROR(msg);
        }

        return FB.SUCCESS(attndsPage);
    }

    public static long testTimestamp = 0;

    public static class SigninParam{
        public SigninParam(@NotBlank @Size(max = 50) String cipher, @Valid @NotNull Location location) {
            this.cipher = cipher;
            this.location = location;
        }

        public String getCipher() {
            return cipher;
        }

        public Location getLocation() {
            return location;
        }

        public void setCipher(String cipher) {
            this.cipher = cipher;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public SigninParam() {
        }

        @NotBlank(groups = All.class)
        @Size(max=50,groups = All.class)
        private String cipher;
        @NotNull(groups = All.class)
        @Valid
        private Location location;

        public interface All{}
    }

    /**
     * @api {post} /api/attnd/signin attnd_signin
     * @apiName attnd_signin
     * @apiGroup Attnd
     * @apiDescription student sign in attendance
     *
     * @apiParam {String} cipher cipher for attendance
     * @apiParamExample {json} Req:
     * {"cipher":"A574AQ","location":{"latitude":35.4,"longitude":174.4,"accuracy":30.0}}
     *
     *
     *
     * @apiSuccess {Number} data 1-->ok 2-->location beyond 3-->expired
     * @apiSuccessExample {json} Resp:
     * {"code":1000,"msg":"","data":1}
     *
     *
     * @apiError (Error-Code) 3001 attnd not exist
     * @apiError (Error-Code) 3002 口令类型 不对应 考勤的实际类型
     * @apiError (Error-Code) 3003 already signin
     * @apiError (Error-Code) 3008 user is the creator
     *
     */
    /***/
    @PostMapping("/attnd/signin")
    public FB signIn(@RequestAttribute("attnd") Session session,
                     @Validated({Location.Location_Struct.class,SigninParam.All.class})
                     @RequestBody SigninParam param){
        //just chk user
        boolean userExist = userService.ChkUserExist(session.getOpenid());
        if (!userExist){
            return FB.SYS_ERROR("user not exist");
        }

        char attnd_type = param.cipher.charAt(0);

        if (!Utils.chkCipherType(attnd_type))
            return FB.PARAM_INVALID("SignIn cipher param unknown type");

        //chk attnd status correspond exclude del
        Attnd attnd = attndService.ChkAttnd(param.cipher);
        if (attnd==null){
            return new FB(Code.ATTND_NOT_EXIST);
        }
        if (!(Utils.GetTypeViaStatus(attnd.getStatus())==attnd_type)){
            return new FB(Code.ATTND_CIPHER_NOT_CORRESPOND,"attnd status to cipher type not correspond");
        }

        //chk user whether has signed in
        boolean hasSignIn = signInService.ChkUserHasSignIn(session.getOpenid(),param.cipher);
        if (hasSignIn){
            return new FB(Code.ATTND_HAS_SIGNIN);
        }

        //if user is attnd creator --> return
        if (attnd.getTeacher_id()==session.getUserID()){
            return new FB(Code.SIGNIN_CREATOR);
        }


        //judge sign in whether success CHK LOCATION CHK TIME
        SignIn signIn = new SignIn();
        signIn.setOpenid(session.getOpenid());
        signIn.setCipher(param.cipher);
        signIn.setLocation(param.location);
        int signInFlag = Utils.calSignInState(
                attnd,param.location,AttndController.testTimestamp==0?System.currentTimeMillis():AttndController.testTimestamp,
                configBean.getMeter_limit(),signIn);

        signIn.setStatus(signInFlag);
        boolean signInSuccess = signInService.AddSignInRecord(signIn);
        if (!signInSuccess){
            return FB.DB_FAILED("SignIn AddSignInRecord failed");
        }
        return FB.SUCCESS(signInFlag);
    }

    /**
     * @api {get} /api/attnd/situation chkAttndSituation
     * @apiName chkAttndSituation
     * @apiGroup Attnd
     *
     * @apiUse Pagination
     * @apiParamExample {String} Req:
     * cipher=A7184&signin_status=true&page=1&page_size=10
     *
     *
     * @apiSuccess {Number} count record total count
     * @apiSuccess {Number=1,2,3} attnd_status studnet attendance status 1-> ok 2-> location beyond 3 -> time expired
     * @apiSuccessExample {json} Resp:
     * {"code":1000,"msg":"","data":{"my_signin":{"openid":"ox111","stu_id":"1506200023","name":"xiaoming","attnd_status":1,"distance":53.14},"present_count":2,"count":10,"attnds":[{"openid":"ox111","stu_id":"1506200023","name":"xiaoming","attnd_status":1,"distance":53.14},{"openid":"ox222","stu_id":"1506200024","name":"zhangli","attnd_status":1,"distance":23.14}]}}
     *
     */
    /***/
    @GetMapping("/attnd/situation")
    public FB chkAttndSituation(
            @RequestAttribute("attnd") Session session,
            @Min(0) @RequestAttribute("start") int start,
            @Min(1) @RequestAttribute("rows") int rows,
            @NotBlank @Size(max = 50) @RequestParam("cipher") String cipher,
            @Range(min = Code.SIGNIN_ALL,max = Code.SIGNIN_EXPIRED) @RequestParam("signin_status") int signinStatus
    ){

        //chk user's signin info
        //null means nothing
        AttndState userSignIn = signInService.ChkSignInInfo(cipher,session.getOpenid());
        if (userSignIn==null)
            userSignIn = new AttndState();


        //chk sign in list
        AttndState[] attndStates;
        attndStates = signInService.ChkSignInList(cipher,start,rows,signinStatus);
        if (attndStates==null){
            return FB.SYS_ERROR("attndStates null");
        }

        //chk count
        int recTotalCount = signInService.CountSignInList(cipher,signinStatus);

        HashMap<String,Object> fbJson = new HashMap<>();

        fbJson.put("count",recTotalCount);
        fbJson.put("my_signin",userSignIn);
        fbJson.put("attnds",attndStates);
        return FB.SUCCESS(fbJson);
    }


    /**
     * @api {post} /api/attnd/del delAttnd
     * @apiName delAttnd
     * @apiGroup Attnd
     * @apiDescription [Condition]:only when the attnd has expired and user should the creator
     * [Effected]:
     * 1.ChkAttnd
     * 2.ChkHisAttndName
     * 3.ChkHisAttndAddr
     * 4.ChkAttndListByUser
     * 5.ChkAttndList_SigninByUser
     *
     * @apiParamExample {String} Req:
     * cipher=GZXQAS
     *
     * @apiError (Error-Code) 3001 attnd not exist
     * @apiError (Error-Code) 3005 attnd has del
     * @apiError (Error-Code) 3006 attnd is ongoing
     * @apiError (Error-Code) 3009 attnd not creator
     */
    /***/
    @PostMapping("/attnd/del")
    public FB delAttnd(
            @RequestAttribute("attnd") Session session,
            @RequestBody MultiValueMap<String,String> formData
    ){
        String cipher = formData.getFirst("cipher");
        if (cipher==null||cipher.equals("")||cipher.length()>50){
            return FB.PARAM_INVALID("cipher invalid");
        }

        long nowTimeStamp = testTimestamp==0?System.currentTimeMillis():testTimestamp;

        Attnd attnd = attndService.ChkAttndStatus(cipher);
        if (attnd==null){
            return new FB(Code.ATTND_NOT_EXIST);
        }
        if (attnd.getStatus()==Code.ATTND_DEL){
            return new FB(Code.ATTND_HAS_DEL);
        }

        //not end by creator and not expired --> ongoing
        if (attnd.getStatus()!=Code.ATTND_END && !Utils.chkAttndEnd(nowTimeStamp,attnd.getStart_time(),attnd.getLast())){
            return new FB(Code.ATTND_ONGOING);
        }

        if (session.getUserID()!=attnd.getTeacher_id()){
            return new FB(Code.ATTND_NOT_CREATOR);
        }

        if(!attndService.UpdAttndStatus(cipher,Code.ATTND_DEL,session.getUserID())){
            return FB.DB_FAILED("UpdAttndStatus failed");
        }

        return FB.SUCCESS();
    }

    /**
     * @api {post} /api/signin/status/upd updSignSituation
     * @apiName updAttndStatus
     * @apiGroup Attnd
     * @apiDescription [Condition]:only when the attnd not del & has expired or end by creator
     *
     * @apiParam {String{1..50}} cipher
     * @apiParam {String} openid openid for student who signin
     * @apiParam {Number=1,2,3,4} attnd_status student attendance status
     * @apiParamExample {String} Req:
     * cipher=A714Q&openid=oid123&attnd_status=4
     *
     * @apiError (Error-Code) 3001 attnd not exist
     * @apiError (Error-Code) 3005 attnd has del
     * @apiError (Error-Code) 3006 attnd is going
     * @apiError (Error-Code) 3009 attnd not creator
     */
    /***/
    @PostMapping("/signin/status/upd")
    public FB updSituation(
            @RequestAttribute("attnd") Session session,
            @RequestBody MultiValueMap<String,String> formData
    ){
        String cipher = formData.getFirst("cipher");
        if (cipher==null||cipher.equals("")||cipher.length()>50){
            return FB.PARAM_INVALID("cipher invalid");
        }
        String signin_openid = formData.getFirst("openid");
        if (signin_openid==null||signin_openid.equals("")||signin_openid.length()>50){
            return FB.PARAM_INVALID("openid invalid");
        }
        String attnd_statusRaw = formData.getFirst("attnd_status");
        if (attnd_statusRaw==null||attnd_statusRaw.equals("")){
            return FB.PARAM_INVALID("attnd_status invalid null or empty");
        }
        int attnd_status;
        try {
            attnd_status = Integer.parseInt(attnd_statusRaw);
        } catch (NumberFormatException e) {
            return FB.PARAM_INVALID("attnd_status to int failed "+e.getMessage());
        }
        if (attnd_status<Code.SIGNIN_OK || attnd_status>Code.SIGNIN_NOT_EXIST){
            return FB.PARAM_INVALID("attnd_status invalid");
        }

        //chk attnd whether be del
        long nowTimeStamp = testTimestamp==0?System.currentTimeMillis():testTimestamp;
        Attnd attnd = attndService.ChkAttndStatus(cipher);
        if (attnd==null){
            return new FB(Code.ATTND_NOT_EXIST);
        }
        if (attnd.getStatus()==Code.ATTND_DEL){
            return new FB(Code.ATTND_HAS_DEL);
        }
        //chk attnd whether ongoing
        if (attnd.getStatus()!=Code.ATTND_END && !Utils.chkAttndEnd(nowTimeStamp,attnd.getStart_time(),attnd.getLast())){
            return new FB(Code.ATTND_ONGOING);
        }


        //chk user whether creator
        if (attnd.getTeacher_id()!=session.getUserID()){
            return new FB(Code.ATTND_NOT_CREATOR);
        }

        //chk user whether signin
        if (!signInService.ChkUserHasSignIn(signin_openid,cipher)){
            return new FB(Code.ATTND_HASNOT_SIGNIN);
        }

        if(!signInService.UpdSignInSituation(cipher,signin_openid,attnd_status)){
            return FB.DB_FAILED("UpdSignInSituation failed");
        }

        return FB.SUCCESS();
    }


    /**
     * @api {post} /api/attnd/end endAttnd
     * @apiName endAttnd
     * @apiGroup Attnd
     * @apiDescription
     * [Condition]:only when
     * 1. the attnd ongoing
     * 2. user should the creator
     * 3. the attnd not be del
     * [Effected]:
     * all about expired judge
     *
     * @apiParam {String{1..50}} cipher
     * @apiParamExample {String} Req:
     * cipher=A714Q
     *
     * @apiError (Error-Code) 3001 attnd not exist
     * @apiError (Error-Code) 3005 attnd has del
     * @apiError (Error-Code) 3012 attnd has be end by creator
     * @apiError (Error-Code) 3009 attnd not creator
     */
    /***/
    @PostMapping("/attnd/end")
    public FB endAttnd(
            @RequestAttribute("attnd") Session session,
            @RequestBody MultiValueMap<String,String> formData
    ){
        String cipher = formData.getFirst("cipher");
        if (cipher==null||cipher.equals("")||cipher.length()>50){
            return FB.PARAM_INVALID("cipher invalid");
        }

        long nowTimeStamp = testTimestamp==0?System.currentTimeMillis():testTimestamp;

        Attnd attnd = attndService.ChkAttndStatus(cipher);
        if (attnd==null){
            return new FB(Code.ATTND_NOT_EXIST);
        }
        if (attnd.getStatus()==Code.ATTND_DEL){
            return new FB(Code.ATTND_HAS_DEL);
        }

        if (attnd.getStatus()==Code.ATTND_END){
            return new FB(Code.ATTND_HAS_BE_END);
        }


        if (session.getUserID()!=attnd.getTeacher_id()){
            return new FB(Code.ATTND_NOT_CREATOR);
        }

        if(!attndService.UpdAttndStatus(cipher,Code.ATTND_END,session.getUserID())){
            return FB.DB_FAILED("UpdAttndStatus failed");
        }

        return FB.SUCCESS();
    }
}
