package com.lzy.attnd.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.lzy.attnd.constant.Code;
import com.lzy.attnd.exception.DBProcessException;
import com.lzy.attnd.model.User;
import com.lzy.attnd.service.UserService;
import com.lzy.attnd.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.sql.*;


@Repository
public class UserRepository implements UserService {
    private final JdbcTemplate jdbcTemplate;

    private final static Logger logger = LoggerFactory.getLogger(UserRepository.class);

    @Autowired
    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean InsOrUpdUserInfo(User user)  throws DataAccessException {
        ObjectMapper mapper = new ObjectMapper();
        //test will failed if not this sentence
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        String remarkJson;
        try {
            remarkJson = mapper.writeValueAsString(user.getRemark());
        }catch (JsonProcessingException jpe){
            throw new DBProcessException("writeValueAsString failed "+jpe.getMessage());
        }

        this.jdbcTemplate.update("INSERT INTO user(name,openid,stuid,remark) VALUES(?,?,?,?) ON DUPLICATE KEY UPDATE name=?, stuid=?;",
                user.getName(),user.getOpenid(),user.getStu_id(),remarkJson,user.getName(),user.getStu_id());
        return true;
    }

    @Override
    public int InsIgnoreUserInfo(User user){
        String remarkJson = Utils.ObjectToJson(user.getRemark());
        if (remarkJson==null){
            logger.error("InsIgnoreUserInfo remark to json failed");
            return 0;
        }

        GeneratedKeyHolder holder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement statement = con.prepareStatement("INSERT IGNORE INTO user(name,openid,stuid,remark) VALUES(?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, user.getName());
                statement.setString(2, user.getOpenid());
                statement.setString(3, user.getStu_id());
                statement.setString(4, remarkJson);
                return statement;
            }
        }, holder);

        if (holder==null||holder.getKey()==null)
            return 0;

        int idAfterIns = holder.getKey().intValue();

        return idAfterIns;
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
            //TODO remark in this place is a String
            user = this.jdbcTemplate.queryForObject("SELECT id,name,stuid,remark,status from user where openid=?",new Object[]{openid},
                    (rs, rowNum) -> new User(rs.getInt("id"),rs.getString("name"),openid,rs.getObject("remark"),rs.getInt("status"),rs.getString("stuid"))
                    );

        } catch (EmptyResultDataAccessException erdae) {
            return null;
        }
        return user;
    }

    @Override
    public boolean ChkUserExist(String openid) throws DataAccessException {
        try {
            this.jdbcTemplate.queryForObject("SELECT 1 FROM user WHERE openid=?",new Object[]{openid},int.class);
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
        return true;
    }

}
