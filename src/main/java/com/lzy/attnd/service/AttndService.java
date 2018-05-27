package com.lzy.attnd.service;


import com.lzy.attnd.constant.Code;
import com.lzy.attnd.model.Attnd;
import com.lzy.attnd.model.PaginationAttnd;
import org.hibernate.validator.constraints.Range;
import org.springframework.dao.DataAccessException;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Service
@Validated
public interface AttndService {
    @Validated({
            Attnd.StartTime.class,Attnd.Last.class,Attnd.Location_Struct.class,Attnd.AddrName.class,Attnd.Name.class,
            Attnd.TeacherID.class,Attnd.Status.class,Attnd.Remark.class, Attnd.TeacherName.class})
    String AddAttnd(@Valid Attnd attnd) throws DataAccessException;

    //exclude by del
    @Validated({Attnd.All.class,Attnd.BaseAll.class})
    @Valid Attnd ChkAttnd(@NotBlank(groups = Attnd.All.class) String cipher) throws DataAccessException;

    //exclude by del
    @NotNull String[] ChkHisAttndName(@Min(1) int userID, @Min(1) int limit)throws DataAccessException;

    //exclude by del
    @NotNull String[] ChkHisAttndAddr(@Min(1) int userID, @Min(1) int limit)throws DataAccessException;

    //exclude by del
    @NotNull PaginationAttnd ChkAttndListByUser(@Min(1) int userID, @Min(0) int start, @Min(1) int rows,@NotNull String query) throws DataAccessException;

    //exclude by del
    @NotNull PaginationAttnd ChkAttndList_SigninByUser(@NotBlank String signIn_openid, @Min(0) int start, @Min(1) int rows, @NotNull String query) throws DataAccessException;

    boolean UpdAttndStatus(@NotBlank String cipher, @Range(min = Code.ATTND_DEL,max = Code.ATTND_DEL) int status, @Min(1) int creatorID) throws DataAccessException;

    @Validated({Attnd.Status.class,Attnd.StartTime.class,Attnd.Last.class,Attnd.Cipher.class,Attnd.TeacherID.class})
    @Valid @Nullable
    Attnd ChkAttndStatus(@NotBlank(groups = Attnd.Cipher.class) String cipher) throws DataAccessException;
}
