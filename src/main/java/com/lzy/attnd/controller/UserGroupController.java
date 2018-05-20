package com.lzy.attnd.controller;

import com.lzy.attnd.service.UserGroupService;
import com.lzy.attnd.utils.FeedBack;
import com.lzy.attnd.utils.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class UserGroupController {


    private final UserGroupService userGroupService;

    @Autowired
    public UserGroupController(UserGroupService userGroupService) {
        this.userGroupService = userGroupService;
    }

    /**
     * @api {get} /api/group/name chkUserGroupName
     * @apiName chkGroupNameByUser
     * @apiGroup Group
     *
     * @apiSuccessExample {json} Resp:
     * {"code":1000,"msg":"","data":["计科151","网工151"]}
     */
    /***/
    @GetMapping("/group/name")
    public FeedBack chkGroupNameByUser(
            @RequestAttribute("attnd") Session session
    ){
        if (session.getUserID()<=0){
            return FeedBack.USER_NOT_EXIST("");
        }
        String[] groupNames =userGroupService.ChkGroupByUser(session.getUserID(),50);
        if (groupNames==null){
            return FeedBack.SYS_ERROR("chkGroupNameByUser groupNames null ");
        }
        return FeedBack.SUCCESS(groupNames);
    }

    /**
     * @apiDeprecated
     * @api {get} /api/group chkUserGroup
     * @apiName chkAttnd
     * @apiGroup Group
     *
     * @apiParam {Number{1-}} group_id id for attendance
     * @apiParamExample {String} Req:
     * group_id=1248
     *
     * @apiSuccessExample {json} Resp:
     * {"code":1000,"msg":"","data":{"group_info":{"id":100,"name":"计科151","status":2,"creator":"lzp"},"user_list":[{"openid":"ox111","stu_id":"1506200023","name":"xiaoming"},{"openid":"ox222","stu_id":"1506200024","name":"dedark"}]}}
     */


    /**
     * @apiDeprecated
     * @api {post} /api/delgroup delUserGroup
     * @apiName delUserGroup
     * @apiGroup Group
     *
     * @apiParam {Number{1-}} group_id id for attendance
     * @apiParamExample {String} Req:
     * group_id=1248
     *
     *
     */

    /**
     * @apiDeprecated
     * @api {post} /api/group/adduser addUserToGroup
     * @apiName addUserToGroup
     * @apiGroup Group
     * @apiDescription get cipher
     *
     * @apiParam {Number{1-}} group_id id for attendance
     * @apiParamExample {String} Req:
     * group_id=1248
     *
     *
     * @apiSuccess {String} cipher 口令 标识位(标识录入/考勤)+通过62进制时间戳后3位+group_id 的62进制表示
     * @apiSuccessExample {json} Resp:
     * {"code":1000,"msg":"","data":{"cipher":"S154812"}}
     */
}
