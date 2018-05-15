package com.lzy.attnd.controller;

import com.lzy.attnd.model.Attnd;
import com.lzy.attnd.service.AttndService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Validated
public class AttndController {

    private final AttndService attndService;

    @Autowired
    public AttndController(AttndService attndService) {
        this.attndService = attndService;
    }

    /**
     * @api {post} /api/attnd addAttnd/updAttnd
     * @apiName addAttnd/updAttnd
     * @apiGroup Attnd
     *
     * @apiParam {Number{0-}} attnd_id id!=0->update，id==0->create
     * @apiParam {String{0..50}} attnd_name 考勤名称
     * @apiParam {Number{0-}} start_time need check start_time + last > now
     * @apiParam {Number{0-1440}} last attendance last time unit->minutes
     * @apiParam {Number{-90-90}} latitude float
     * @apiParam {Number{-180-180}} longitude float
     * @apiParam {Number{0-}} accuracy float
     * @apiParam {String} group_name attnd group only work when creating , tag this attnd is member adding
     * @apiParam {String{0..50}} teacher_name if user exist -> do nothing
     * @apiParamExample {json} Req-create:
     * {"attnd_id":0,"attnd_name":"操作系统","start_time":15577418,"last":20,"location":{"latitude":35.4,"longitude":174.4,"accuracy":30.0},"teacher_name":"wjx","group_name":"计科151"}
     *
     * @apiParamExample {json} Req-update:
     * {"attnd_id":12335,"attnd_name":"操作系统","start_time":15577418,"last":50,"location":{"latitude":35.4,"longitude":174.4,"accuracy":30.0},"teacher_name":"wjx","group_name":"计科151"}
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
    public String addAttnd(@Valid @RequestBody Attnd attnd){
        //TODO...
        return "ok";
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
     * {"code":1000,"msg":"","data":{"attnd_id":12335,"cipher":"JQS52","attnd_name":"操作系统","start_time":15577418,"last":50,"location":{"latitude":35.4,"longitude":174.4,"accuracy":30.0},"teacher_name":"wjx","group_name":"计科151"}}
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
