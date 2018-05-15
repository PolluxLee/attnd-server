package com.lzy.attnd.service;

import com.lzy.attnd.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Service
@Validated
public interface UserService {
    @Validated({User.Name.class,User.Openid.class,User.Remark.class,User.StuIDNotNUll.class})
    boolean InsOrUpdUserInfo(
            @Valid @NotNull(groups = User.Name.class) User user) throws DataAccessException;

    @Validated({User.Name.class,User.Openid.class,User.StuID.class})
    boolean UpdUserInfoByOpenid(
            @Valid @NotNull(groups = User.Name.class) User user) throws DataAccessException;

    @Validated(User.All.class)
    @Nullable @Valid User FindUserByOpenid(
            @NotBlank(groups = User.All.class) String openid) throws DataAccessException;
}
