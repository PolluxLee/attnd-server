package com.lzy.attnd.controller;

import com.lzy.attnd.constant.Code;
import com.lzy.attnd.model.User;
import com.lzy.attnd.model.UserGroup;
import com.lzy.attnd.service.UserGroupService;
import com.lzy.attnd.utils.FB;
import com.lzy.attnd.utils.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.HashMap;

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
    public FB chkGroupNameByUser(
            @RequestAttribute("attnd") Session session
    ){
        if (session.getUserID()<=0){
            return FB.USER_NOT_EXIST("");
        }
        String[] groupNames =userGroupService.ChkGroupNameByCreator(session.getUserID(),50);
        if (groupNames==null){
            return FB.SYS_ERROR("chkGroupNameByUser groupNames null ");
        }
        return FB.SUCCESS(groupNames);
    }

    /**
     *
     * @api {get} /api/group/list chkUserGrouplist
     *
     * @apiName chkUserGrouplist
     * @apiGroup Group
     *
     * @apiSuccessExample {json} Resp:
     * {"code":1000,"msg":"","data":[{"id":100,"name":"计科151","status":2,"creator_name":"lzp"},{"id":101,"name":"网工151","status":1,"creator_name":"lzp"}]}
     */
    /***/
    @GetMapping("/group/list")
    public FB chkUserGroupList(
            @RequestAttribute("attnd") Session session
    ){
        if (session.getUserID()<=0){
            return FB.USER_NOT_EXIST("");
        }
        UserGroup[] groups =userGroupService.ChkGroupListByUser(session.getUserID());
        if (groups==null){
            return FB.SYS_ERROR("chkUserGroupList groups null ");
        }
        return FB.SUCCESS(groups);
    }

    /**
     * @api {get} /api/group chkUserGroup
     * @apiName chkUserGroup
     * @apiGroup Group
     *
     * @apiParam {Number{1-}} group_id id for attendance
     * @apiParamExample {String} Req:
     * group_id=1248
     *
     * @apiSuccessExample {json} Resp:
     * {"code":1000,"msg":"","data":{"id":100,"name":"计科151","status":2,"creator":"lzp"}}
     */
    /***/
    @GetMapping("/group")
    public FB chkUserGroup(
            @Min (1) @RequestParam("group_id") int groupID,
            @RequestAttribute("attnd") Session session
    ){
        //TODO privilege control

        UserGroup userGroup = userGroupService.ChkGroupInfoByGroupID(groupID);
        if (userGroup==null){
            return new FB(Code.GROUP_NOTEXIST);
        }

        return FB.SUCCESS(userGroup);
    }

    /**
     * @api {get} /api/group/userlist chkGroupUserList
     * @apiName chkGroupUserList
     * @apiGroup Group
     *
     * @apiUse Pagination
     * @apiParam {Number{1-}} group_id id for attendance
     * @apiParamExample {String} Req:
     * group_id=1248
     *
     * @apiSuccessExample {json} Resp:
     * {"code":1000,"msg":"","data":{"count":10,"user_list":[{"openid":"ox111","stu_id":"1506200023","name":"xiaoming"},{"openid":"ox222","stu_id":"1506200024","name":"dedark"}]}}
     */
    /****/
    @GetMapping("/group/userlist")
    @Transactional
    public FB chkGroupUserList(
            @Min(0) @RequestAttribute("start") int start,
            @Min(1) @RequestAttribute("rows") int rows,
            @Min (1) @RequestParam("group_id") int groupID
    ){
        //TODO privilege control
        User[] users = userGroupService.ChkGroupUserlist(groupID,start,rows);
        if (users==null){
            return FB.SYS_ERROR("chkGroupUserList users null");
        }

        int count = userGroupService.CountUserInGroup(groupID);

        HashMap<String,Object> fb = new HashMap<>();
        fb.put("user_list",users);
        fb.put("count",count);
        return FB.SUCCESS(fb);
    }

    /**
     * @api {post} /api/group/del delUserGroup
     * @apiName delUserGroup
     * @apiGroup Group
     * @apiDescription [Effected]:
     * 1. add user to group (AddUserToGroup)
     * 2. chk group exist by name (ChkUserGroupExistByName)
     * 3. chk his group name (ChkGroupNameByCreator)
     * 4. chk group list    (ChkGroupListByUser)
     * [Condition]: user is the creator of the group
     *
     *
     * @apiParam {Number{1-}} group_id id for attendance
     * @apiParamExample {String} Req:
     * group_id=1248
     *
     */
    /***/
    @PostMapping("/group/del")
    public FB delGroup(
            @RequestAttribute("attnd") Session session,
            @RequestBody MultiValueMap<String,String> formData
    ){
        String rawGroupID = formData.getFirst("group_id");
        if (rawGroupID==null||Integer.valueOf(rawGroupID)<=0){
            return FB.PARAM_INVALID("rawGroupID invalid");
        }
        int groupID = Integer.valueOf(rawGroupID);

        if (session.getUserID()<=0){
            return FB.USER_NOT_EXIST("");
        }

        if(!userGroupService.UpdGroupStatus(Code.GROUP_DEL,groupID,session.getUserID())){
            return FB.DB_FAILED("UpdGroupStatus failed");
        }

        return FB.SUCCESS();
    }

    /**
     * @apiDeprecated
     * @api {post} /api/group/user/add addUserToGroup
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
