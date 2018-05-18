package com.lzy.attnd.service;


import com.lzy.attnd.model.Attnd;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Service
@Validated
public interface AttndService {
    @Validated({
            Attnd.StartTime.class,Attnd.Last.class,Attnd.Location_Struct.class,Attnd.AddrName.class,Attnd.Name.class,
            Attnd.TeacherID.class,Attnd.Status.class,Attnd.Remark.class,Attnd.GroupNameNotNull.class, Attnd.TeacherName.class})
    String AddAttnd(@Valid Attnd attnd) throws DataAccessException;

    @Validated({Attnd.All.class,Attnd.BaseAll.class})
    @Valid Attnd ChkAttnd(@NotBlank(groups = Attnd.All.class) String cipher) throws DataAccessException;
}
