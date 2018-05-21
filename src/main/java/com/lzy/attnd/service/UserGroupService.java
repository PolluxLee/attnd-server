package com.lzy.attnd.service;


import com.lzy.attnd.constant.Code;
import com.lzy.attnd.model.User;
import com.lzy.attnd.model.UserGroup;
import org.hibernate.validator.constraints.Range;
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
    //exclude group del
    @Validated({UserGroup.Creatorid.class,UserGroup.Name.class})
    @Min(value = 0,groups = UserGroup.Name.class) int ChkUserGroupExistByName(@Valid @NotNull(groups = UserGroup.Name.class) UserGroup userGroup) throws DataAccessException;

    @Validated({UserGroup.AllButID.class,UserGroup.BaseAll.class})
    boolean AddNewGroup(@Valid UserGroup userGroup) throws DataAccessException;

    @Min(0) int CountUserInGroup(@Min(1) int groupID) throws DataAccessException;

    //exclude group del
    @NotNull String[] ChkGroupNameByCreator(@Min(1) int creatorID, @Min(1) int limit) throws DataAccessException;

    //exclude group del
    @Validated({UserGroup.All.class,UserGroup.Status.class})
    @NotNull(groups = UserGroup.All.class) @Valid UserGroup[] ChkGroupListByUser(@Min(value = 1,groups = UserGroup.All.class) int creatorID) throws DataAccessException;

    @Validated({UserGroup.All.class,UserGroup.Status.class})
    @Valid UserGroup ChkGroupInfoByGroupID(@Min(value = 1,groups = UserGroup.All.class) int groupID) throws DataAccessException;

    @Validated({User.ID.class,User.Status.class,User.Openid.class,User.Name.class,User.StuID.class})
    @NotNull(groups = User.ID.class) User[] ChkGroupUserlist(@Min(value = 1,groups = User.ID.class) int groupID, @Min(0) int start, @Min(1) int rows) throws DataAccessException;

    boolean UpdGroupStatus(@Range(min = Code.GROUP_DEL,max = Code.GROUP_DEL) int status,@Min(1) int groupID,@Min(1) int creatorID) throws DataAccessException;
}
