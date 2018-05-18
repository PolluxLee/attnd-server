package com.lzy.attnd.repository;

import com.lzy.attnd.constant.Code;
import com.lzy.attnd.model.Attnd;
import com.lzy.attnd.service.AttndService;
import com.lzy.attnd.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

@Repository
public class AttndRepository implements AttndService {

    private final static Logger logger = LoggerFactory.getLogger(UserRepository.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AttndRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Transactional
    @Override
    public String AddAttnd(Attnd attnd) throws DataAccessException {
        String locationJson = attnd.getLocationJson();
        String remarkJson = attnd.getRemarkJson();
        if (locationJson==null||remarkJson==null){
            logger.error("AddAttnd remark or location to json failed");
            return "";
        }

        //because cipher is not null
        GeneratedKeyHolder holder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement statement = con.prepareStatement("INSERT INTO attnd(starttime,lasttime,location,addrname,teacherid,status,remark,cipher,groupname) VALUES(?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                statement.setLong(1, attnd.getStart_time());
                statement.setInt(2, attnd.getLast());
                statement.setString(3, locationJson);
                statement.setString(4,  attnd.getAddr_name());
                statement.setInt(5,  attnd.getTeacher_id());
                statement.setInt(6,  attnd.getStatus());
                statement.setString(7, remarkJson);
                statement.setString(8, Utils.LongToBase62LastK(System.currentTimeMillis(),10));
                statement.setString(9, attnd.getGroup_name());
                return statement;
            }
        }, holder);

        int idAfterIns = holder.getKey().intValue();

        String cipher = Utils.CalCipher(Utils.GetTypeViaStatus(attnd.getStatus()),idAfterIns);
        if (cipher==null||cipher.equals("")){
            logger.warn("AddAttnd cipher invalid");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return "";
        }

        int effectedRows = this.jdbcTemplate.update("UPDATE attnd SET cipher=? WHERE id=?",cipher,idAfterIns);
        if (effectedRows != 1){
            logger.error("AddAttnd update cipher failed");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return "";
        }

        attnd.setAttnd_id(idAfterIns);
        return cipher;
    }
}
