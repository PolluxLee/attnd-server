package com.lzy.attnd.service;


import com.lzy.attnd.model.User;
import com.lzy.attnd.model.UserGroup;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Service
@Validated
public interface UserGroupService {
    @Validated({UserGroup.Creatorid.class,UserGroup.Name.class})
    boolean ChkUserGroupExistByName(@Valid @NotNull(groups = UserGroup.Name.class) UserGroup userGroup) throws DataAccessException;

    @Validated({UserGroup.All.class,UserGroup.BaseAll.class})
    boolean AddNewGroup(@Valid UserGroup userGroup) throws DataAccessException;

    boolean AddUserToGroup(@NotBlank String openid,@Min(1) int groupID) throws DataAccessException;
}
