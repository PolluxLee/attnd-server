package com.lzy.attnd.repository;

import com.lzy.attnd.exception.DBProcessException;
import com.lzy.attnd.model.SignIn;
import com.lzy.attnd.service.SignInService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotBlank;


@Repository
public class SignInRepository implements SignInService {
    private final static Logger logger = LoggerFactory.getLogger(SignInRepository.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SignInRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean AddSignInRecord(SignIn signIn) throws DataAccessException {
        String locStr = signIn.getLocation().getLocationJson();
        if (locStr==null||locStr.length()<=0){
            throw new DBProcessException("AddSignInRecord locStr invalid");
        }
        int effectedRows = this.jdbcTemplate.update("INSERT INTO signin(openid, name, cipher, location, remark,status) VALUES (?,?,?,?,?,?)",
                new Object[]{signIn.getOpenid(),signIn.getName(),signIn.getCipher(),locStr,signIn.getRemarkJson(),signIn.getStatus()});
        return effectedRows == 1;
    }

    @Override
    public boolean ChkUserHasSignIn(String openid, String cipher) throws DataAccessException {
        try {
            this.jdbcTemplate.queryForObject("SELECT 1 FROM signin WHERE openid=? AND cipher=?",new Object[]{openid,cipher},int.class);
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
        return true;
    }
}
