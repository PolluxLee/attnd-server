package com.lzy.attnd.repository;

import com.lzy.attnd.exception.DBProcessException;
import com.lzy.attnd.model.AttndState;
import com.lzy.attnd.model.SignIn;
import com.lzy.attnd.service.SignInService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;


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
        int effectedRows = this.jdbcTemplate.update("INSERT INTO signin(openid, cipher, location, remark,status,distance) VALUES (?,?,?,?,?,?)",
                new Object[]{signIn.getOpenid(),signIn.getCipher(),locStr,signIn.getRemarkJson(),signIn.getStatus(),signIn.getDistance()});
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

    private String statusExcludeHandle(int statusExclude){
        if (statusExclude<=-1){
            return "";
        }
        return " AND signin.status<>"+Integer.toString(statusExclude);
    }

    @Override
    public AttndState[] ChkSignInList(String cipher, int start, int count, int groupID,int statusExclude) throws DataAccessException {
        //groupID<=0 --> all student


        String query;
        Object[] args;
        if (groupID<=0){
            query = "SELECT signin.openid,distance AS dist,signin.status,stuid,name FROM signin JOIN user on signin.openid=user.openid WHERE cipher=? %s ORDER BY stuid ASC LIMIT ?,?;";
            args = new Object[]{cipher,start,count};
        }else {
            query="SELECT user.openid,coalesce(distance, -1) AS dist,coalesce(signin.status, 4) AS status,stuid,name\n" +
                    "FROM user LEFT JOIN signin on signin.openid=user.openid\n" +
                    "WHERE JSON_CONTAINS(user.groupid,?,'$')=1 AND (cipher=? or cipher is null) %s ORDER BY stuid ASC LIMIT ?,?;";
            args = new Object[]{Integer.toString(groupID),cipher,start,count};
        }

        query = String.format(query,statusExcludeHandle(statusExclude));

        List<AttndState> attndStateList=this.jdbcTemplate.query(
                query,
                args,
                (rs, i) -> new AttndState(rs.getString("openid"),rs.getString("name"),rs.getString("stuid"),rs.getInt("status"),rs.getDouble("dist")));

        if (attndStateList==null){
            throw new DBProcessException("ChkSignInList attndStateList null");
        }
        return attndStateList.toArray(new AttndState[0]);
    }

    @Override
    public int CountSignInList(String cipher,int statusExclude) throws DataAccessException {
        String query = String.format("SELECT COUNT(id) FROM signin WHERE cipher=? %s",statusExcludeHandle(statusExclude));

        return this.jdbcTemplate.queryForObject(query,new Object[]{cipher},int.class);
    }
}