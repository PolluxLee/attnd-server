package com.lzy.attnd.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lzy.attnd.configure.ConfigBean;
import com.lzy.attnd.constant.Code;
import com.lzy.attnd.model.*;
import com.lzy.attnd.service.AttndService;
import com.lzy.attnd.service.SignInService;
import com.lzy.attnd.service.UserGroupService;
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
    private final UserGroupService userGroupService;
    private final UserService userService;
    private final SignInService signInService;

    private final ConfigBean configBean;

    @Autowired
    public AttndController(AttndService attndService,UserGroupService userGroupService, UserService userService, SignInService signInService, ConfigBean configBean) {
        this.attndService = attndService;
        this.userGroupService = userGroupService;
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
     * @apiParam {Number{0-1440}} last attendance last time unit->minutes
     * @apiParam {Number{-90-90}} latitude float
     * @apiParam {Number{-180-180}} longitude float
     * @apiParam {Number{0-}} accuracy float
     * @apiParam {String{0..50}} [group_name] attnd group only work when creating , tag this attnd is member adding
     * @apiParam {String{0..50}} addr_name location name
     * @apiParam {String{0..50}} teacher_name if user exist -> do nothing
     * @apiParamExample {json} Req-create:
     * {"attnd_name":"操作系统","start_time":1526826235000,"last":20,"location":{"latitude":35.4,"longitude":174.4,"accuracy":30.0},"addr_name":"外环西路","teacher_name":"wjx","group_name":"计科151"}
     *
     * @apiSuccess {String} cipher 口令 标识位(标识录入/考勤)+通过62进制时间戳后3位+ (attnd_id (when no_group) ||group_id ) 的62进制表示
     * @apiSuccessExample {json} Resp:
     * {"code":1000,"msg":"","data":{"attnd_id":5415,"cipher":"G548QC","userinfo":{"id":1,"openid":"fdsafe51515","name":"lzp"}}}
     */
    /***/
    @PostMapping("/attnd")
    public FB addAttnd(
            HttpSession httpSession,
            @RequestAttribute("attnd") Session session,
            @Validated({Attnd.Name.class,Attnd.StartTime.class,Attnd.Last.class,Attnd.Location_Struct.class,Attnd.AddrName.class,
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

        int attndStatus = Code.ATTND_NORMAL;

        //no group fill
        int groupID = 0;
        if (attnd.getGroup_name()==null || attnd.getGroup_name().equals("")){
            attnd.setGroup_name("");
            attndStatus = Code.ATTND_NOGROUP;
        }else{
            if (attnd.getGroup_name().length()>50){
                return FB.PARAM_INVALID("addAttnd group name length > 50");
            }
            //chk group exist by name
            UserGroup userGroup = new UserGroup();
            userGroup.setName(attnd.getGroup_name());
            userGroup.setCreator_id(user.getId());
            userGroup.setCreator_name(user.getName());
            groupID =userGroupService.ChkUserGroupExistByName(userGroup);
            if (groupID<=0){
                //create a new group
                userGroup.setRemark(new Object());
                boolean addGroupSuccess = userGroupService.AddNewGroup(userGroup);
                if (!addGroupSuccess){
                    return FB.DB_FAILED("addAttnd AddNewGroup failed");
                }
                attndStatus = Code.ATTND_ENTRY;
            }
        }


        attnd.setStatus(attndStatus);

        //add attnd
        attnd.setRemark(new Object());
        attnd.setTeacher_id(session.getUserID());
        String cipher = attndService.AddAttnd(attnd,groupID);
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
     * cipher=GZXQAS
     *
     * @apiSuccessExample {json} Resp:
     * {"code":1000,"msg":"","data":{"teacher_id":1,"attnd_id":123,"cipher":"GZXQAS","status":1,"attnd_name":"操作系统","start_time":15577418,"last":20,"location":{"latitude":35.4,"longitude":174.4,"accuracy":30.0},"addr_name":"外环西路","teacher_name":"wjx","group_name":"计科151"}}
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
     * {"code":1000,"msg":"","data":{"attnds":[{"attnd_id":1,"attnd_name":"操作系统1","start_time":1522512000,"last":20,"location":{"latitude":23.4,"longitude":174.4,"accuracy":30.0},"addr_name":"外环西路","group_name":"网工151","teacher_name":"lzy","teacher_id":1,"cipher":"Gwvk1"}],"count":1}}
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

    /**
     * @api {post} /api/attnd/signin attnd_signin
     * @apiName attnd_signin
     * @apiGroup Attnd
     * @apiDescription student sign in attendance
     *
     * @apiParam {String} cipher cipher for attendance
     * @apiParamExample {json} Req:
     * {"cipher":"X574AQ","location":{"latitude":35.4,"longitude":174.4,"accuracy":30.0}}
     *
     * @apiParamExample {json} Req-Attnd-S:
     * {"cipher":"S574AQ"}
     *
     * @apiSuccess {Number} data 1-->ok 2-->location beyond 3-->expired
     * @apiSuccessExample {json} Resp:
     * {"code":1000,"msg":"","data":1}
     *
     *
     * @apiError (Error-Code) 3001 attnd not exist
     * @apiError (Error-Code) 3002 口令类型 不对应 考勤的实际类型
     * @apiError (Error-Code) 3003 already signin
     * @apiError (Error-Code) 3007 user not belong the group
     * @apiError (Error-Code) 3008 user is the creator
     *
     */
    /***/
    @PostMapping("/attnd/signin")
    public FB signIn(@RequestAttribute("attnd") Session session,
                     @RequestBody @NotNull JsonNode root){
        JsonNode cipherNode = root.get("cipher");
        if (cipherNode == null || !cipherNode.isTextual()){
            return FB.PARAM_INVALID("cipher invalid");
        }
        String cipher = cipherNode.asText();
        if (cipher == null ||cipher.equals("") || cipher.length()>50){
            return FB.PARAM_INVALID("cipher invalid empty or too long");
        }

        Location signLoc = null;
        if (cipher.charAt(0)!=Code.CIPHER_SINGLE){
            ObjectMapper mapper = new ObjectMapper();
            if (!root.hasNonNull("location")){
                return FB.PARAM_INVALID("location invalid in root prop");
            }
            try {
                signLoc = mapper.treeToValue(root.get("location"),Location.class);
            } catch (JsonProcessingException e) {
                return FB.PARAM_INVALID("location invalid in treeToValue");
            }
            if (signLoc==null || signLoc.getAccuracy()<0||signLoc.getLatitude()<-90||signLoc.getLatitude()>90||signLoc.getLongitude()<-180||signLoc.getLongitude()>180){
                return FB.PARAM_INVALID("location invalid in children prop");
            }
        }


        //--------- chk param complete

        //just chk user
        boolean userExist = userService.ChkUserExist(session.getOpenid());
        if (!userExist){
            return FB.SYS_ERROR("user not exist");
        }

        Attnd attnd = null;
        char attnd_type = cipher.charAt(0);

        if (!Utils.chkCipherType(attnd_type))
            return FB.PARAM_INVALID("SignIn cipher param unknown type");

        //attnd_type = S
        if (attnd_type == Code.CIPHER_SINGLE){
            //get group ID from cipher
            //cipher length - type(1) - timestamp(3)
            int groupID = ((int) Utils.Base62LastKToLong(cipher, cipher.length() - 1 - 3));
            if (groupID == -1){
                return FB.SYS_ERROR("get groupid from cipher failed");
            }
            if(!userService.AddUserToGroupByID(session.getOpenid(),groupID)){
                return FB.DB_FAILED("AddUserToGroupByID in CIPHER_SINGLE failed");
            }
            return FB.SUCCESS();
        }

        //attnd_type = A,G,N
        //chk attnd status correspond
        attnd = attndService.ChkAttnd(cipher);
        if (attnd==null){
            return new FB(Code.ATTND_NOT_EXIST);
        }
        if (!(Utils.GetTypeViaStatus(attnd.getStatus())==attnd_type)){
            return new FB(Code.ATTND_CIPHER_NOT_CORRESPOND,"attnd status to cipher type not correspond");
        }

        //chk user whether has signed in
        boolean hasSignIn = signInService.ChkUserHasSignIn(session.getOpenid(),cipher);
        if (hasSignIn){
            return new FB(Code.ATTND_HAS_SIGNIN);
        }

        //if user is attnd creator --> return
        if (attnd.getTeacher_id()==session.getUserID()){
            return new FB(Code.SIGNIN_CREATOR);
        }

        //if type = A --> judge user is this group
        if (attnd_type == Code.CIPHER_ATTND ){
            int groupID = ((int) Utils.Base62LastKToLong(cipher, cipher.length() - 1 - 3));
            if (groupID<=0){
                return FB.SYS_ERROR("get groupid from cipher invalid");
            }

            if (!userService.ChkUserIsGroupByGroupID(session.getUserID(),groupID)){
                return new FB(Code.SIGNIN_NOT_BELONG_GROUP);
            }
        }

        if (attnd_type==Code.CIPHER_ENTRY){
            if(!userService.AddUserToGroup(session.getOpenid(),attnd.getGroup_name(),attnd.getTeacher_id())){
                return FB.DB_FAILED("AddUserToGroup in CIPHER_ENTRY failed");
            }
        }

        //judge sign in whether success CHK LOCATION CHK TIME
        SignIn signIn = new SignIn();
        signIn.setOpenid(session.getOpenid());
        signIn.setCipher(cipher);
        signIn.setLocation(signLoc);
        int signInFlag = Utils.calSignInState(
                attnd,signLoc,AttndController.testTimestamp==0?System.currentTimeMillis():AttndController.testTimestamp,
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
     * cipher=A7184&fail_only=true&page=1&page_size=10
     *
     *
     * @apiSuccess {Number} count record total count
     * @apiSuccess {Number} present_count 实到人数 only work in type A & fail_only=false
     * @apiSuccess {Number=1,2,3,4} attnd_status studnet attendance status 1-> ok 2-> location beyond 3 -> time expired 4 -> not exist
     * @apiSuccessExample {json} Resp:
     * {"code":1000,"msg":"","data":{"count":10,"attnds":[{"openid":"ox111","stu_id":"1506200023","name":"xiaoming","attnd_status":1,"distance":53.14},{"openid":"ox222","stu_id":"1506200024","name":"zhangli","attnd_status":1,"distance":23.14}]}}
     *
     */
    /***/
    @GetMapping("/attnd/situation")
    public FB chkAttndSituation(
            @Min(0) @RequestAttribute("start") int start,
            @Min(1) @RequestAttribute("rows") int rows,
            @NotBlank @Size(max = 50) @RequestParam("cipher") String cipher,
            @RequestParam("fail_only") boolean fail_only
    ){
        int groupID = 0;
        AttndState[] attndStates;
        switch (cipher.charAt(0)){
            case Code.CIPHER_ATTND:{
                //get the last groupid
                groupID = ((int) Utils.Base62LastKToLong(cipher, cipher.length() - 1 - 3));
            }
            case Code.CIPHER_ENTRY:
            case Code.CIPHER_NOGROUP:{
                //chk sign in list
                attndStates = signInService.ChkSignInList(cipher,start,rows,groupID,fail_only?Code.SIGNIN_OK:-1);
                break;
            }
            default:
                return FB.SYS_ERROR("unknown cipher type");
        }

        if (attndStates==null){
            return FB.SYS_ERROR("attndStates null");
        }

        HashMap<String,Object> fbJson = new HashMap<>();

        int recTotalCount;
        if (groupID<=0){
            //TYPE G/N
            recTotalCount = signInService.CountSignInList(cipher,fail_only?Code.SIGNIN_OK:-1);
        }else{
            //TYPE A
            int signInFailCount = signInService.CountSignInListWithGroup(cipher,groupID,Code.SIGNIN_OK);
            int totalGroupUserCount =  userGroupService.CountUserInGroup(groupID);
            if (fail_only){
                //chk user not signin in group
                recTotalCount = signInFailCount;
            }else{
                recTotalCount = totalGroupUserCount;
            }
            //present_count = totoal_user_count - userNotSigninCount 实到人数=组内总人数-签到失败人数
            fbJson.put("present_count",totalGroupUserCount-signInFailCount);
        }


        fbJson.put("count",recTotalCount);
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

        if (nowTimeStamp<=attnd.getStart_time()+attnd.getLast()*60*1000){
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
     * @apiDescription [Condition]:only when the attnd not del & has expired /-- or stop by creator--/
     *
     * @apiParam {String{1..50}} cipher
     * @apiParam {String} openid openid for student who signin
     * @apiParam {Number=1,4} attnd_status student attendance status 1-> ok 4-> not ok
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
        if (attnd_status!=Code.SIGNIN_OK && attnd_status!=Code.SIGNIN_NOT_EXIST){
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
        if (nowTimeStamp<=attnd.getStart_time()+attnd.getLast()*60*1000){
            return new FB(Code.ATTND_ONGOING);
        }

        //chk user whether creator
        if (attnd.getTeacher_id()!=session.getUserID()){
            return new FB(Code.ATTND_NOT_CREATOR);
        }

        if(!signInService.UpdSignInSituation(cipher,signin_openid,attnd_status)){
            return FB.DB_FAILED("UpdSignInSituation failed");
        }

        return FB.SUCCESS();
    }
}
