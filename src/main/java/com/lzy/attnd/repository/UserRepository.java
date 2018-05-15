package com.lzy.attnd.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.lzy.attnd.model.User;
import com.lzy.attnd.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;


@Repository
public class UserRepository implements UserService {
    private final JdbcTemplate jdbcTemplate;

    private final static Logger logger = LoggerFactory.getLogger(UserRepository.class);

    @Autowired
    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean InsOrUpdUserInfo(User user) {
        ObjectMapper mapper = new ObjectMapper();
        //test will failed if not this sentence
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        String remarkJson;
        try {
            remarkJson = mapper.writeValueAsString(user.getRemark());
        }catch (JsonProcessingException jpe){
            logger.warn("AddUser failed: "+jpe.getMessage());
            jpe.printStackTrace();
            return false;
        }

        this.jdbcTemplate.update("INSERT INTO user(name,openid,stuid,remark) VALUES(?,?,?,?) ON DUPLICATE KEY UPDATE name=?, stuid=?;",user.getName(),user.getOpenid(),user.getStu_id(),remarkJson,user.getName(),user.getStu_id());
        return true;
    }

    //condition
    @Override
    public boolean UpdUserInfoByOpenid(User user) {
        int effectedRows = this.jdbcTemplate.update("UPDATE user set name=? ,stuid=?  WHERE openid=?;",user.getName(),user.getStu_id(),user.getOpenid());
        return effectedRows == 1;
    }

    //user null -> not found
    @Override
    public User FindUserByOpenid(String openid) {
        User user;
        try {
            user = this.jdbcTemplate.queryForObject("SELECT id,name,stuid,remark,status from user where openid=?",new Object[]{openid},
                    (rs, rowNum) -> new User(rs.getInt("id"),rs.getString("name"),openid,rs.getObject("remark"),rs.getInt("status"),rs.getString("stuid")));
        } catch (EmptyResultDataAccessException erdae) {
            return null;
        }
        return user;
    }
}
