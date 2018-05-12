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

    private AttndService attndService;

    @Autowired
    public AttndController(AttndService attndService) {
        this.attndService = attndService;
    }

    /**
     * @api {post} /api/attnd addAttnd/updAttnd
     * @apiName addAttnd/updAttnd
     * @apiGroup Attnd
     *
     * @apiParam {Number{0-}} attnd_id id不为0->更新，id为0->创建
     * @apiParam {String{0..50}} attnd_name 考勤名称
     * @apiParam {Number{0-1440}} last attendance last time unit->minutes
     * @apiParam {Number{-90-90}} latitude float
     * @apiParam {Number{-180-180}} longitude float
     * @apiParam {Number{0-}} accuracy float
     * @apiParam {String{0..50}} teacher_name 字段不为空字符串则更新个人信息，空字符串则不理会
     * @apiParamExample {json} Req-create:
     * {"attnd_id":0,"attnd_name":"操作系统","start_time":15577418,"last":20,"location":{"latitude":35.4,"longitude":174.4,"accuracy":30.0},"teacher_name":"wjx"}
     *
     * @apiParamExample {json} Req-update:
     * {"attnd_id":12335,"attnd_name":"操作系统","start_time":15577418,"last":50,"location":{"latitude":35.4,"longitude":174.4,"accuracy":30.0},"teacher_name":"wjx"}
     *
     * @apiSuccess {String} cipher 口令 通过62进制时间戳后3位+attnd_id 的62进制表示
     * @apiSuccessExample {json} Resp-create:
     * {"code":1000,"msg":"","data":{"attnd_id":5415,"cipher":"X548QC"}}
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
}
