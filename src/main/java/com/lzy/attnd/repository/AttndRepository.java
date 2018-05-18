package com.lzy.attnd.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.lzy.attnd.constant.Code;
import com.lzy.attnd.exception.DBProcessException;
import com.lzy.attnd.model.Attnd;
import com.lzy.attnd.service.AttndService;
import com.lzy.attnd.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.sql.*;
import java.util.Map;

@Repository
public class AttndRepository implements AttndService {

    private final static Logger logger = LoggerFactory.getLogger(AttndRepository.class);

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
            throw new DBProcessException("location or remark invalid");
        }

        //because cipher is not null
        GeneratedKeyHolder holder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement("INSERT INTO attnd(starttime,lasttime,location,addrname,teacherid,status,remark,cipher,groupname,teachername,name) VALUES(?,?,?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, attnd.getStart_time());
            statement.setInt(2, attnd.getLast());
            statement.setString(3, locationJson);
            statement.setString(4,  attnd.getAddr_name());
            statement.setInt(5,  attnd.getTeacher_id());
            statement.setInt(6,  attnd.getStatus());
            statement.setString(7, remarkJson);
            statement.setString(8, Utils.LongToBase62LastK(System.currentTimeMillis(),10));
            statement.setString(9, attnd.getGroup_name());
            statement.setString(10, attnd.getTeacher_name());
            statement.setString(11, attnd.getAttnd_name());
            return statement;
        }, holder);

        int idAfterIns = holder.getKey().intValue();

        String cipher = Utils.CalCipher(Utils.GetTypeViaStatus(attnd.getStatus()),idAfterIns);
        if (cipher==null||cipher.equals("")){
            logger.warn("AddAttnd cipher invalid");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new DBProcessException("cal cipher failed");
        }

        int effectedRows = this.jdbcTemplate.update("UPDATE attnd SET cipher=? WHERE id=?",cipher,idAfterIns);
        if (effectedRows != 1){
            logger.error("AddAttnd update cipher failed");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new DBProcessException("UPDATE effected not 1");
        }

        attnd.setAttnd_id(idAfterIns);
        return cipher;
    }

    @Override
    public Attnd ChkAttnd(String cipher) throws DataAccessException {
        Attnd attnd;

        try {
            attnd = this.jdbcTemplate.queryForObject("SELECT id,name,starttime,lasttime,location,addrname,teacherid,teachername,groupname,status,remark from attnd where cipher=?",
                    new Object[]{cipher}, (rs, i) -> {
                        String locStr = rs.getString("location");
                        Attnd attndInner = new Attnd();
                        try {
                            ObjectMapper objectMapper = new ObjectMapper();
                            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

                            JsonNode root = objectMapper.readTree(locStr);
                            if (root== null){
                                throw new DBProcessException("jsonNode null");
                            }
                            attndInner.getLocation().setAccuracy(root.get("accuracy").floatValue());
                            attndInner.getLocation().setLatitude(root.get("latitude").floatValue());
                            attndInner.getLocation().setLongitude(root.get("longitude").floatValue());

                        } catch (IOException ioe){
                            logger.error("ChkAttnd mapRow failed io "+ioe.getMessage());
                            throw new DBProcessException("location map failed in io");
                        }

                        return new Attnd(rs.getObject("remark"),rs.getInt("status"),
                                rs.getInt("id"),rs.getString("name"),rs.getLong("starttime"),
                                rs.getInt("lasttime"),attndInner.getLocation(),rs.getString("addrname"),
                                rs.getString("groupname"),rs.getString("teachername"),rs.getInt("teacherid"),cipher);

                    });
        } catch (EmptyResultDataAccessException erdae) {
            return null;
        }
        return attnd;
    }
}


/*
(rs, rowNum) ->



*/
