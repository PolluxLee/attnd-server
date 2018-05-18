package com.lzy.attnd.repository;

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
import javax.validation.constraints.NotBlank;

@Repository
public class UserGroupRepository implements UserGroupService {

    private final static Logger logger = LoggerFactory.getLogger(UserRepository.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserGroupRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public boolean ChkUserGroupExistByName(UserGroup userGroup) throws DataAccessException {
        try {
            this.jdbcTemplate.queryForObject("SELECT 1 FROM usergroup WHERE name=? AND creatorid=?",new Object[]{userGroup.getName(),userGroup.getCreator_id()},Integer.class);
        } catch (EmptyResultDataAccessException ere) {
            return false;
        }
        return true;
    }

    @Override
    public boolean AddNewGroup(UserGroup userGroup) throws DataAccessException {
        String remarkJson = userGroup.getRemarkJson();
        if (remarkJson==null){
            logger.error("AddNewGroup remark to json failed");
            return false;
        }
        int effectedRows = this.jdbcTemplate.update("INSERT INTO usergroup(name, creatorname, creatorid, remark) VALUES (?,?,?,?)",new Object[]{userGroup.getName(),userGroup.getCreator_name(),userGroup.getCreator_id(),remarkJson});
        return effectedRows==1;
    }
}
