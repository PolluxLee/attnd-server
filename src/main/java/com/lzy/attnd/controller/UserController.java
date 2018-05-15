package com.lzy.attnd.controller;

import com.lzy.attnd.configure.ConfigBean;
import com.lzy.attnd.constant.Code;
import com.lzy.attnd.model.User;
import com.lzy.attnd.service.UserService;
import com.lzy.attnd.service.WechatService;
import com.lzy.attnd.utils.FeedBack;
import com.lzy.attnd.utils.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
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
        Object sessionBrowser = session.getAttribute(configBean.getSession_key());
        if (sessionBrowser != null) {
            System.out.println("session exist:" + sessionBrowser.toString());
        }else{
            System.out.println("session not exist");
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                System.out.println(cookie.getName() + " : " + cookie.getValue());
            }
        }
        return "index";
    }

    @GetMapping("/mocklogin")
    public String mocklogin(HttpServletRequest request, HttpSession session){
        session.setAttribute(configBean.getSession_key(),new Session("lzy",0,"oid","wxsessionkey"));
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
     * {"code":2002,"msg": "","data":{"id":1,"openid":"fdsafe51515","stu_id":"1506200023","name":"lzp","remark":{},"status":0}}
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
            user = new User(0,"",wxLoginFb.getOpenid());
        }

        //update the newest user regardless of the session exist
        //first session key -> app session key
        //second session key -> wx session key
        session.setAttribute(configBean.getSession_key(),new Session(user.getName(),user.getStatus(),wxLoginFb.getOpenid(),wxLoginFb.getSession_key()));

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
     * {"code":1000,"msg": "","data":{"id":1,"openid":"fdsafe51515","stu_id":"1506200023","name":"lzp","remark":{},"status":0}}
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
     * @api {post} /api/user/info?type=1 AddUserInfo/UpdUserInfo
     * @apiName AddUserInfo/UpdUserInfo
     * @apiGroup User

     *
     * @apiParam {Number=1,2} type add/upd 1-> add 2-> upd
     *
     * @apiParamExample {json} Request-body:
     * {"name":"lzy","stu_id":"1506200023"}
     *
     */
    /***/
    @PostMapping("/user/info")
    public FeedBack addUser(@Validated(User.C.Add.class) @RequestBody User user){
        //TODO need session
        return null;
    }

}
