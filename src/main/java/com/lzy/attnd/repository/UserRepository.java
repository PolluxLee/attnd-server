package com.lzy.attnd.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    public boolean AddUser(User user) {
        ObjectMapper mapper = new ObjectMapper();
        String remarkJson;
        try {
            remarkJson = mapper.writeValueAsString(user.getRemark());
        }catch (JsonProcessingException jpe){
            logger.warn("AddUser failed: "+jpe.getMessage());
            jpe.printStackTrace();
            return false;
        }

        int effectedRows = this.jdbcTemplate.update("INSERT INTO user(name,openid,remark) VALUES(?,?,?);",user.getName(),user.getOpenid(),remarkJson);
        return effectedRows == 1;
    }

    //condition
    @Override
    public boolean UpdUserNameByOpenid(String openid,String userName) {
        int effectedRows = this.jdbcTemplate.update("UPDATE user set name=? WHERE openid=?;",userName,openid);
        return effectedRows == 1;
    }

    //user null -> not found
    @Override
    public User FindUserByOpenid(String openid) {
        User user;
        try {
            user = this.jdbcTemplate.queryForObject("SELECT id,name,remark,status from user where openid=?",new Object[]{openid},
                    (rs, rowNum) -> new User(rs.getInt("id"),rs.getString("name"),openid,rs.getObject("remark"),rs.getInt("status")));
        } catch (EmptyResultDataAccessException erdae) {
            return null;
        }
        return user;
    }
}
