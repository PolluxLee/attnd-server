package com.lzy.attnd.repository;

import com.lzy.attnd.exception.DBProcessException;
import com.lzy.attnd.model.UserGroup;
import com.lzy.attnd.service.UserGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Repository
public class UserGroupRepository implements UserGroupService {

    private final static Logger logger = LoggerFactory.getLogger(UserGroupRepository.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserGroupRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public int ChkUserGroupExistByName(UserGroup userGroup) throws DataAccessException {
        int id;
        try {
            id = this.jdbcTemplate.queryForObject("SELECT id FROM usergroup WHERE name=? AND creatorid=?",new Object[]{userGroup.getName(),userGroup.getCreator_id()},Integer.class);
        } catch (EmptyResultDataAccessException ere) {
            return 0;
        }
        return id;
    }

    @Override
    public boolean AddNewGroup(UserGroup userGroup) throws DataAccessException {
        String remarkJson = userGroup.getRemarkJson();
        if (remarkJson==null){
            logger.error("AddNewGroup remark to json failed");
            throw new DBProcessException("remark json invalid");
        }
        int effectedRows = this.jdbcTemplate.update("INSERT INTO usergroup(name, creatorname, creatorid, remark) VALUES (?,?,?,?)",new Object[]{userGroup.getName(),userGroup.getCreator_name(),userGroup.getCreator_id(),remarkJson});
        return effectedRows==1;
    }

    @Override
    public int CountUserInGroup(int groupID) throws DataAccessException {
        return this.jdbcTemplate.queryForObject("SELECT COUNT(id) FROM user WHERE JSON_CONTAINS(groupid,?,'$')=1",new Object[]{Integer.toString(groupID)},int.class);
    }

    @Override
    public String[] ChkGroupByUser(int creatorID,int limit) throws DataAccessException {
        List<String> list= this.jdbcTemplate.queryForList("SELECT name FROM usergroup WHERE creatorid=? ORDER BY createdat desc LIMIT ?",String.class,creatorID,limit);
        if (list==null){
            String msg = "ChkGroupByUser list null";
            logger.error(msg);
            throw new DBProcessException(msg);
        }
        return list.toArray(new String[0]);
    }
}
