package com.lzy.attnd.controller;

import com.lzy.attnd.configure.ConfigBean;
import com.lzy.attnd.constant.Code;
import com.lzy.attnd.model.Attnd;
import com.lzy.attnd.model.User;
import com.lzy.attnd.model.UserGroup;
import com.lzy.attnd.service.AttndService;
import com.lzy.attnd.service.UserGroupService;
import com.lzy.attnd.service.UserService;
import com.lzy.attnd.utils.FeedBack;
import com.lzy.attnd.utils.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@Validated
public class AttndController {

    private final AttndService attndService;
    private final UserGroupService userGroupService;
    private final UserService userService;
    private final ConfigBean configBean;

    @Autowired
    public AttndController(AttndService attndService,UserGroupService userGroupService, UserService userService, ConfigBean configBean) {
        this.attndService = attndService;
        this.userGroupService = userGroupService;
        this.userService = userService;
        this.configBean = configBean;
    }

    /**
     * @api {post} /api/attnd addAttnd
     * @apiName addAttnd
     * @apiGroup Attnd
     *
     * @apiParam {String{0..50}} attnd_name 考勤名称
     * @apiParam {String{0..50}} addr_name location name
     * @apiParam {Number{0-}} start_time need check start_time + last > now
     * @apiParam {Number{0-1440}} last attendance last time unit->minutes
     * @apiParam {Number{-90-90}} latitude float
     * @apiParam {Number{-180-180}} longitude float
     * @apiParam {Number{0-}} accuracy float
     * @apiParam {String{0..50}} [group_name] attnd group only work when creating , tag this attnd is member adding
     * @apiParam {String{0..50} addr_name location name
     * @apiParam {String{0..50}} teacher_name if user exist -> do nothing
     * @apiParamExample {json} Req-create:
     * {"attnd_name":"操作系统","start_time":15577418,"last":20,"location":{"latitude":35.4,"longitude":174.4,"accuracy":30.0},"addr_name":"外环西路","teacher_name":"wjx","group_name":"计科151"}
     *
     * @apiSuccess {String} cipher 口令 标识位(标识录入/考勤)+通过62进制时间戳后3位+attnd_id 的62进制表示
     * @apiSuccessExample {json} Resp-create:
     * {"code":1000,"msg":"","data":{"attnd_id":5415,"cipher":"A548QC"}}
     *
     * @apiSuccessExample {json} Resp-update:
     * {"code":1000,"msg":""}
     */
    /***/
    @PostMapping("/attnd")
    public FeedBack addAttnd(
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
                return FeedBack.DB_FAILED("addAttnd InsIgnoreUserInfo failed");
            }
            user.setId(id);
        }
        session.setUserID(user.getId());
        session.setName(attnd.getTeacher_name());

        //if user info has updated -> update session
        httpSession.setAttribute(configBean.getSession_key(),session);

        int attndStatus = Code.ATTND_NORMAL;

        //no group fill
        if (attnd.getGroup_name()==null || attnd.getGroup_name().equals("")){
            attnd.setGroup_name("");
            attndStatus = Code.ATTND_NOGROUP;
        }else{
            if (attnd.getGroup_name().length()>50){
                return FeedBack.PARAM_INVALID("addAttnd group name length > 50");
            }
            //chk group exist by name
            UserGroup userGroup = new UserGroup(attnd.getGroup_name(),user.getId(),user.getName());
            boolean groupExist =userGroupService.ChkUserGroupExistByName(userGroup);
            if (!groupExist){
                //create a new group
                userGroup.setRemark(new Object());
                boolean addGroupSuccess = userGroupService.AddNewGroup(userGroup);
                if (!addGroupSuccess){
                    return FeedBack.DB_FAILED("addAttnd AddNewGroup failed");
                }
                attndStatus = Code.ATTND_ENTRY;
            }
        }


        attnd.setStatus(attndStatus);

        //add attnd
        attnd.setRemark(new Object());
        attnd.setTeacher_id(session.getUserID());
        String cipher = attndService.AddAttnd(attnd);
        if (cipher==null||cipher.equals("")){
            return FeedBack.DB_FAILED("addAttnd cipher invalid");
        }

        if (attnd.getAttnd_id()<=0){
            return FeedBack.DB_FAILED("addAttnd AddAttnd no id return");
        }

        HashMap<String,Object> fbJson = new HashMap<>();
        fbJson.put("attnd_id",attnd.getAttnd_id());
        fbJson.put("cipher",cipher);
        return FeedBack.SUCCESS(fbJson);
    }


    /**
     * @api {post} /api/delattnd delAttnd
     * @apiName delAttnd
     * @apiGroup Attnd
     *
     * @apiParam {Number{1-}} attnd_id id for attendance
     * @apiParamExample {String} Req:
     * attnd_id=1248
     *
     *
     */



    /**
     * @api {get} /api/attnd chkAttnd
     * @apiName chkAttnd
     * @apiGroup Attnd
     *
     * @apiParam {Number{1-}} attnd_id id for attendance
     * @apiParamExample {String} Req:
     * attnd_id=1248
     *
     * @apiSuccessExample {json} Resp:
     * {"code":1000,"msg":"","data":{"attnd_id":12335,"cipher":"JQS52","attnd_name":"操作系统","start_time":15577418,"last":50,"location":{"latitude":35.4,"longitude":174.4,"accuracy":30.0},"addr_name":"中环西路","teacher_name":"wjx","group_name":"计科151"}}
     */

    /**
     * @api {get} /api/attnd/hisname chkAttnd_hisname
     * @apiName chkAttnd_hisname
     * @apiGroup Attnd
     * @apiDescription get latest top 6
     *
     *
     * @apiParam {Number{1-}} attnd_id id for attendance
     * @apiParamExample {String} Req:
     * attnd_id=1248
     *
     * @apiSuccessExample {json} Resp:
     * {"code":1000,"msg":"","data":["操作系统","计算机网络"]}
     */


    /**
     * @apiDefine Pagination
     * @apiParam {Number{1..}} page  分页页号 1开始
     * @apiParam {Number{1..}} pagesize  每页长度
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
     * {"code":1000,"msg":"","data":{"count":10,"attnds":[{"attnd_id":12335,"cipher":"JQS52","attnd_name":"操作系统","start_time":15577418,"last":50,"location":{"latitude":35.4,"longitude":174.4,"accuracy":30.0},"teacher_name":"wjx"},{"attnd_id":14475,"cipher":"CX6q52","attnd_name":"计算机网络","start_time":15566788,"last":50,"location":{"latitude":36.4,"longitude":175.4,"accuracy":30.0},"teacher_name":"lzp"}]}}
     *
     */

    /**
     * @api {post} /api/attnd/signin attnd_signin
     * @apiName attnd_signin
     * @apiGroup Attnd
     * @apiDescription student sign in attendance
     *
     * @apiParam {String} cipher cipher for attendance
     * @apiParamExample {String} Req:
     * cipher=X574AQ
     *
     */

    /**
     * @api {get} /api/attnd/situation chkAttndSituation
     * @apiName chkAttndSituation
     * @apiGroup Attnd
     *
     * @apiParam {Number{1-}} attnd_id id for attendance
     * @apiParamExample {String} Req:
     * attnd_id=1248
     *
     *
     * @apiSuccessParam {Number=1,2} attnd_status studnet attendance status 1-> ok 2-> not ok
     * @apiSuccessExample {json} Resp:
     * {"code":1000,"msg":"","data":{"count":10,"attnds":[{"openid":"ox111","stu_id":"1506200023","name":"xiaoming","attnd_status":1},{"openid":"ox222","stu_id":"1506200024","name":"zhangli","attnd_status":1}]}}
     *
     */


    /**
     * @api {post} /api/attnd/situation updAttndSituation
     * @apiName updAttndSituation
     * @apiGroup Attnd
     *
     * @apiParam {Number{1-}} attnd_id id for attendance
     * @apiParam {String} stu_id id for student to upd not empty
     * @apiParam {Number=1,2} attnd_status studnet attendance status 1-> ok 2-> not ok
     * @apiParamExample {String} Req:
     * attnd_id=1248&stu_id=1506200023&attnd_status=2
     *
     *
     */
}
