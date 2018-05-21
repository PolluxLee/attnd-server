package com.lzy.attnd.service;

import com.lzy.attnd.model.User;
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
public interface UserService {
    @Validated({User.Name.class,User.Openid.class,User.Remark.class,User.StuIDNotNUll.class})
    boolean InsOrUpdUserInfo(
            @Valid @NotNull(groups = User.Name.class) User user) throws DataAccessException;

    @Validated({User.Name.class,User.Openid.class,User.Remark.class,User.StuIDNotNUll.class})
    int InsIgnoreUserInfo(
            @Valid @NotNull(groups = User.Name.class) User user) throws DataAccessException;

    @Validated({User.Name.class,User.Openid.class,User.StuID.class})
    boolean UpdUserInfoByOpenid(
            @Valid @NotNull(groups = User.Name.class) User user) throws DataAccessException;

    @Validated({User.Name.class,User.Openid.class,User.ID.class,User.Remark.class,User.Status.class})
    @Nullable @Valid User FindUserByOpenid(
            @NotBlank(groups = User.Openid.class) String openid) throws DataAccessException;

    boolean ChkUserExist(@NotBlank String openid) throws DataAccessException;

    //exclude group del
    boolean AddUserToGroup(@NotBlank String openid,@NotBlank String groupName, @Min(1) int creatorID) throws DataAccessException;
    //exclude group del
    boolean AddUserToGroupByID(@NotBlank String openid,@Min(1) int groupID) throws DataAccessException;

    User[] ChkUserListByGroupID(@Min(1) int groupID) throws DataAccessException;
}
