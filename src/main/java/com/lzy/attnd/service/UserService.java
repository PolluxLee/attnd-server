package com.lzy.attnd.service;

import com.lzy.attnd.model.User;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Validated
public interface UserService {
    boolean AddUser(@Valid User user);
    boolean UpdUserNameByOpenid(@NotBlank String openid,@NotBlank String userName);
    @Nullable User FindUserByOpenid(@NotBlank String openid);
}
