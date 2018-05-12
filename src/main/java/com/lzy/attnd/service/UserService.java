package com.lzy.attnd.service;

import com.lzy.attnd.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Service
@Validated
public interface UserService {
    @Validated(User.S.Ins.class)
    boolean AddUser(@Valid User user) throws DataAccessException;
    boolean UpdUserNameByOpenid(@NotBlank String openid,@NotBlank String userName) throws DataAccessException;
    @Validated(User.S.Query.class)
    @Nullable @Valid User FindUserByOpenid(@NotBlank String openid) throws DataAccessException;
}
