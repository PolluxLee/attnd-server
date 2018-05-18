package com.lzy.attnd.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class UserGroupController {
    /**
     * @api {post} /api/group updUserGroup
     * @apiName UpdUserGroup
     * @apiGroup Group
     *
     * @apiParam {Number{1-}} group_id
     * @apiParam {Number=1,2} status group allow join status 1-> can join 2-> can't join
     * @apiParamExample {json} Request-body-update:
     * {"group_id":100,"name":"计科151","status":2}
     *
     */


    /**
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
