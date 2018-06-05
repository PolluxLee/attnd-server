package com.lzy.attnd.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.lzy.attnd.repository.AttndRepository;
import com.lzy.attnd.utils.FB;
import com.lzy.attnd.utils.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.List;

@RestController
@Validated
public class LoggerController {

    private final static Logger logger = LoggerFactory.getLogger(LoggerController.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LoggerController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public static class FeLogger {
        @NotBlank
        @Size(max = 50)
        public String level;
        @NotBlank
        @Size(max = 1000)
        public String msg;

        public String getLoc() {
            if (loc==null)
                return "";
            return loc;
        }

        public String getOper() {
            if (oper==null)
                return "";
            return oper;
        }

        @Size(max = 50)
        public String loc;
        @Size(max = 50)
        public String oper;
    }

    /**
     * @api {post} /api/fe/log frontend_logger
     * @apiName frontend_logger
     * @apiGroup logger
     *
     * @apiParamExample {json} Req:
     * [{"level":"info","msg":"1000","loc":"pagination/userinfo","oper":"fill in userinfo"},{"level":"error","msg":"1003","loc":"attnd/signin","oper":"student_signin"}]
     *
     * @apiSuccessExample {json} Resp:
     * {"code":1000,"msg":""}
     *
     */
    /***/
    @PostMapping("/fe/log")
    public FB frontEndLogger(
            @RequestAttribute("attnd") Session session,
            @RequestBody @Valid FeLogger[] loggers
    ){
        if (loggers==null || loggers.length<=0){
            return FB.PARAM_INVALID("loggers empty");
        }

        Object[] args = new Object[loggers.length*2];
        ObjectMapper mapper = new ObjectMapper();
        String sessionJson = null;
        String loggersJson = null;
        try {
            sessionJson = mapper.writeValueAsString(session);
            loggersJson = mapper.writeValueAsString(loggers);
        } catch (JsonProcessingException e) {
            String msg = "frontEndLogger writeValueAsString failed: ";
            logger.error(msg+e.getMessage());
            return FB.SYS_ERROR(msg);
        }

        for (int i = 2; i < loggers.length*2; i+=2) {
            args[i]=sessionJson;
        }

        List<String> loggersList;
        try {
            loggersList = split(loggersJson);
        } catch (Exception e) {
            String msg = "frontEndLogger split failed: ";
            logger.error(msg+e.getMessage());
            return FB.SYS_ERROR(msg);
        }
        if (loggersList==null||loggersList.size()<=0||loggersList.size()!=loggers.length){
            String msg = "frontEndLogger arrayEle invalid";
            logger.error(msg);
            return FB.SYS_ERROR(msg);
        }
        String[] loggersArray = loggersList.toArray(new String[0]);

        StringBuilder stringBuilder = new StringBuilder(" INSERT INTO felog(userinfo,loginfo) VALUES ");
        stringBuilder.append(" (?,?) ");
        args[0] = sessionJson;
        args[1] = loggersArray[0];
        for (int i = 1; i < loggers.length; i++) {
            stringBuilder.append(" ,(?,?) ");
            args[i*2+1] = loggersArray[i];
        }

        int effectRows = this.jdbcTemplate.update(
                stringBuilder.toString(),
                args
        );

        if (effectRows <= 0){
            String msg = "frontEndLogger insert zero log";
            logger.error(msg);
            return FB.DB_FAILED(msg);
        }

        return FB.SUCCESS();
    }

    public List<String> split(String jsonArray) throws Exception {
        List<String> splittedJsonElements = new ArrayList<String>();
        ObjectMapper jsonMapper = new ObjectMapper();
        JsonNode jsonNode = jsonMapper.readTree(jsonArray);
        if (jsonNode.isArray()) {
            ArrayNode arrayNode = (ArrayNode) jsonNode;
            for (int i = 0; i <arrayNode.size(); i++) {
                JsonNode individualElement = arrayNode.get(i);
                splittedJsonElements.add(individualElement.toString());
            }
        }
        return splittedJsonElements;
    }
}
