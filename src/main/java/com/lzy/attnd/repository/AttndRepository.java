package com.lzy.attnd.repository;

import com.lzy.attnd.model.Attnd;
import com.lzy.attnd.service.AttndService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AttndRepository implements AttndService {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Override
    public void AddAttnd(Attnd attnd) {

    }
}
