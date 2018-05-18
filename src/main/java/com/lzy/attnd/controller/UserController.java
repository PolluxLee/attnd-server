package com.lzy.attnd.controller;

import com.lzy.attnd.configure.ConfigBean;
import com.lzy.attnd.constant.Code;
import com.lzy.attnd.model.User;
import com.lzy.attnd.service.UserService;
import com.lzy.attnd.service.WechatService;
import com.lzy.attnd.utils.FeedBack;
import com.lzy.attnd.utils.Session;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.SessionScope;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@RestController
@Validated
public class UserController {

    @Autowired
    public UserController(UserService userService, WechatService wechatService, ConfigBean configBean) {
        this.userService = userService;
        this.wechatService = wechatService;
        this.configBean = configBean;
    }

    //testing api
    @GetMapping("/chk/session")
    public String cookie(HttpServletRequest request, HttpSession session) {
        //取出session中的browser
        StringBuilder sb = new StringBuilder();
        Object sessionBrowser = session.getAttribute(configBean.getSession_key());
        if (sessionBrowser != null) {
            sb.append("session exist:" + sessionBrowser.toString()+"\n");
        }else{
            sb.append("session not exist\n");
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                sb.append(cookie.getName() + " : " + cookie.getValue()+" \n");
            }
        }
        return sb.toString();
    }

    @GetMapping("/mocklogin")
    public String mocklogin(
            @Min(1) @RequestParam("id") int id,
            @NotBlank @RequestParam("name") String name,
            @NotBlank @RequestParam("openid") String openid,
            @NotBlank @RequestParam("session_key") String session_key,
            HttpServletRequest request, HttpSession session){
        session.setAttribute(configBean.getSession_key(),new Session(id,name,0,openid,session_key));
        return "ok";
    }


    private final UserService userService;

    private final WechatService wechatService;

    private final ConfigBean configBean;


    /**
     * @api {post} /api/login Login
     * @apiName Login
     * @apiGroup User
     * @apiDescription
     * login and wait for the wx server response , set cookie and response user info
     *
     * @apiParam {Number} code code from wx.login
     * @apiParamExample {String} Request-body:
     * code=12345
     *
     * @apiSuccessExample {json} user-exist:
     * {"code":2002,"msg": "","data":{"id":1,"openid":"fdsafe51515","stu_id":"1506200023","name":"lzp"}}
     *
     * @apiSuccessExample {json} user-not-exist:
     * {"code":2001,"msg": "","data":{"openid":"owxxer4748"}}
     *
     */
    /***/
    @PostMapping(value = "/login",consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public FeedBack login(@RequestBody MultiValueMap<String,String> formData,HttpSession session){
        String code = formData.getFirst("code");
        if (code == null || code.isEmpty()){
            return FeedBack.PARAM_INVALID("code not exist or empty");
        }
        //request wx get openid+session_key
        //TODO...WX...
        WechatService.WxLoginFb wxLoginFb = wechatService.Wx_Login(code);
        if (wxLoginFb == null){
            //TODO WX ERROR
            return FeedBack.SYS_ERROR("Wx_Login failed");
        }

        //find user info
        User user = userService.FindUserByOpenid(wxLoginFb.getOpenid());
        if (user == null){
            user = new User();
            user.setOpenid(wxLoginFb.getOpenid());
        }

        //update the newest user regardless of the session exist
        //first session key -> app session key
        //second session key -> wx session key
        session.setAttribute(configBean.getSession_key(),new Session(user.getId(),user.getName(),user.getStatus(),wxLoginFb.getOpenid(),wxLoginFb.getSession_key()));

        return user.getName().equals("")?new FeedBack<>(Code.USER_NOT_EXIST,"",user):new FeedBack<>(Code.USER_EXIST,"",user);
    }

    /**
     * @api {get} /api/user/info ChkUserInfo
     * @apiName ChkUserInfo
     * @apiGroup User
     *
     * @apiParam {string} openid user openid
     *
     * @apiSuccessExample {json} Resp:
     * {"code":1000,"msg": "","data":{"id":1,"openid":"fdsafe51515","stu_id":"1506200023","name":"lzp"}}
     *
     * @apiError (Error-Code) 2001 user not exist
     */
    /***/
    @GetMapping("/user/info")
    public FeedBack chkUserInfo(@NotBlank @RequestParam("openid") String openid){
        User user = userService.FindUserByOpenid(openid);
        if (user==null){
            return new FeedBack<>(Code.USER_NOT_EXIST,"","find nothing by openid");
        }

        return FeedBack.SUCCESS(user);
    }

    /**
     * @api {post} /api/user/info FillUserInfo
     * @apiName AddUserInfo/UpdUserInfo
     * @apiGroup User
     * @apiDescription
     * if not exists -> insert
     * exists -> update
     *
     * @apiParam {string} name user openid
     * @apiParam {string} [stu_id] user openid
     * @apiParamExample {json} Request-body:
     * {"name":"lzy","stu_id":"1506200023"}
     *
     */
    /***/
    @PostMapping("/user/info")
    public FeedBack addOrUpdUser(
            @RequestAttribute("attnd") Session session,
            @Validated({User.Name.class}) @RequestBody User user){

        user.setRemark(new Object());
        if (user.getStu_id()==null)
            user.setStu_id("");
        user.setOpenid(session.getOpenid());

        boolean success = userService.InsOrUpdUserInfo(user);
        if (!success){
            return FeedBack.DB_FAILED("addOrUpdUser InsOrUpdUserInfo failed");
        }
        return FeedBack.SUCCESS();
    }

}
