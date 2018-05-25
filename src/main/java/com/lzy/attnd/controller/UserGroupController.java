package com.lzy.attnd.controller;

import com.lzy.attnd.constant.Code;
import com.lzy.attnd.model.User;
import com.lzy.attnd.model.UserGroup;
import com.lzy.attnd.service.UserGroupService;
import com.lzy.attnd.utils.FB;
import com.lzy.attnd.utils.Session;
import com.lzy.attnd.utils.Utils;
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
     * @apiDescription order by createdat desc
     *
     * @apiSuccessExample {json} Resp:
     * {"code":1000,"msg":"","data":[{"id":100,"name":"计科151","status":2,"creator_name":"lzp"},{"id":101,"name":"网工151","status":1,"creator_name":"lzp"}]}
     */
    /***/
    @GetMapping("/group/list")
    public FB chkUserGroupList(
            @RequestAttribute("attnd") Session session
    ){
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
     * {"code":1000,"msg":"","data":{"id":100,"name":"计科151","status":2,"creator_name":"lzp"}}
     *
     * @apiError (Error-Code) 4001 group not exist
     */
    /***/
    @GetMapping("/group")
    public FB chkUserGroup(
            @Min (1) @RequestParam("group_id") int groupID
    ){
        //todo right control only the member or the creator
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
        //todo right control only the creator

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
     *
     * @apiError (Error-Code) 4001 group not exist
     * @apiError (Error-Code) 4002 group not belong current user
     * @apiError (Error-Code) 4003 group has been del
     */
    /***/
    @PostMapping("/group/del")
    public FB delGroup(
            @RequestAttribute("attnd") Session session,
            @RequestBody MultiValueMap<String,String> formData
    ){
        String rawGroupID = formData.getFirst("group_id");
        if (rawGroupID==null){
            return FB.PARAM_INVALID("rawGroupID invalid");
        }
        int groupID = 0;
        try {
            groupID = Integer.parseInt(rawGroupID);
        } catch (NumberFormatException e) {
            return FB.PARAM_INVALID("rawGroupID to int failed");
        }
        if (groupID<=0){
            return FB.PARAM_INVALID("groupID invalid");
        }

        UserGroup userGroup = userGroupService.ChkGroupInfoByGroupID(groupID);
        if (userGroup==null){
            return new FB(Code.GROUP_NOTEXIST);
        }

        if (userGroup.getStatus()==Code.GROUP_DEL){
            return new FB(Code.GROUP_HAS_DEL);
        }

        if (userGroup.getCreator_id()!=session.getUserID()){
            return new FB(Code.GROUP_NOT_CREATOR);
        }

        if(!userGroupService.UpdGroupStatus(Code.GROUP_DEL,groupID,session.getUserID())){
            return FB.DB_FAILED("UpdGroupStatus failed");
        }

        return FB.SUCCESS();
    }

    /**
     * @api {post} /api/group/user/add addUserToGroup
     * @apiName addUserToGroup
     * @apiGroup Group
     * @apiDescription get cipher
     * [Conditions]:
     * 1. user is the creator of the group
     * 2. group exist
     * 3. group not del
     *
     * @apiParam {Number{1-}} group_id id for attendance
     * @apiParamExample {String} Req:
     * group_id=1248
     *
     *
     * @apiSuccess {String} cipher 口令 标识位(标识录入/考勤)+通过62进制时间戳后3位+group_id 的62进制表示
     * @apiSuccessExample {json} Resp:
     * {"code":1000,"msg":"","data":"S15812"}
     *
     *
     * @apiError (Error-Code) 4001 group not exist
     * @apiError (Error-Code) 4002 group not belong current user
     * @apiError (Error-Code) 4003 group has been del
     */
    /***/
    @PostMapping("/group/user/add")
    public FB addUserToGroupSingle(
            @RequestAttribute("attnd") Session session,
            @RequestBody MultiValueMap<String,String> formData
    ){
        String group_idRaw = formData.getFirst("group_id");
        if (group_idRaw==null||group_idRaw.equals("")){
            return FB.PARAM_INVALID("group_id RAW invalid null or empty");
        }
        int group_id;
        try {
            group_id = Integer.parseInt(group_idRaw);
        } catch (NumberFormatException e) {
            return FB.PARAM_INVALID("group_id to int failed "+e.getMessage());
        }
        if (group_id<=0){
            return FB.PARAM_INVALID("group_id invalid <=0");
        }

        UserGroup userGroup = userGroupService.ChkGroupInfoByGroupID(group_id);
        if (userGroup==null){
            return new FB(Code.GROUP_NOTEXIST);
        }

        if (userGroup.getStatus()==Code.GROUP_DEL){
            return new FB(Code.GROUP_HAS_DEL);
        }

        if (userGroup.getCreator_id()!=session.getUserID()){
            return new FB(Code.GROUP_NOT_CREATOR);
        }

        String cipher = Utils.CalCipher(Code.CIPHER_SINGLE,userGroup.getId());
        if (cipher==null ||"".equals(cipher)){
            return FB.SYS_ERROR("cipher build failed");
        }

        return FB.SUCCESS(cipher);
    }
}
