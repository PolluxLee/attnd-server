package com.lzy.attnd.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
    public boolean InsOrUpdUserInfo(User user) {
        ObjectMapper mapper = new ObjectMapper();
        //test will failed if not this sentence
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        String remarkJson;
        try {
            remarkJson = mapper.writeValueAsString(user.getRemark());
        }catch (JsonProcessingException jpe){
            logger.warn("AddUser failed: "+jpe.getMessage());
            return false;
        }

        this.jdbcTemplate.update("INSERT INTO user(name,openid,stuid,remark,groupid) VALUES(?,?,?,?,?) ON DUPLICATE KEY UPDATE name=?, stuid=?;",
                user.getName(),user.getOpenid(),user.getStu_id(),remarkJson,"[]",user.getName(),user.getStu_id());
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
                PreparedStatement statement = con.prepareStatement("INSERT IGNORE INTO user(name,openid,stuid,remark,groupid) VALUES(?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, user.getName());
                statement.setString(2, user.getOpenid());
                statement.setString(3, user.getStu_id());
                statement.setString(4, remarkJson);
                statement.setString(5, "[]");
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
            user = this.jdbcTemplate.queryForObject("SELECT id,name,stuid,remark,status,groupid from user where openid=?",new Object[]{openid},
                    (rs, rowNum) -> {
                        String groupsStr = rs.getString("groupid");
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
                        int[] groups = new int[0];
                        try {
                            groups = mapper.readValue(groupsStr,int[].class);
                        } catch (IOException e) {
                            String msg = "FindUserByOpenid groupsStr to object failed: "+e.getMessage();
                            logger.error(msg);
                            throw new DBProcessException(msg);
                        }

                        return new User(rs.getInt("id"),rs.getString("name"),openid,rs.getObject("remark"),rs.getInt("status"),rs.getString("stuid"),groups);
                    }
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

    @Override
    public boolean AddUserToGroup(String openid, String groupName, int creatorID) throws DataAccessException {
        //group exist
        int groupID;
        try {
            groupID = this.jdbcTemplate.queryForObject("SELECT id FROM usergroup WHERE name=? and creatorid=?",new Object[]{groupName,creatorID},int.class);
        } catch (EmptyResultDataAccessException e) {
            throw new DBProcessException("group not exist");
        }

        return this.addUserToGroupByID(openid,groupID);

    }

    private boolean addUserToGroupByID(String openid,int groupID) throws DataAccessException {
        int effectedRows = this.jdbcTemplate.update(
                "UPDATE user SET groupid=JSON_ARRAY_APPEND(groupid,'$',?) WHERE openid=?",
                groupID,openid);
        return effectedRows == 1;
    }

    @Override
    public boolean AddUserToGroupByID(String openid,int groupID) throws DataAccessException {
        //group exist
        try {
            this.jdbcTemplate.queryForObject("SELECT 1 FROM usergroup WHERE id=?",new Object[]{groupID},int.class);
        } catch (EmptyResultDataAccessException e) {
            throw new DBProcessException("group not exist");
        }

        return this.addUserToGroupByID(openid,groupID);
    }

    @Override
    public User[] ChkUserListByGroupID(int groupID) throws DataAccessException {
        return new User[0];
    }
}
