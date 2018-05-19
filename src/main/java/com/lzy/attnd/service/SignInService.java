package com.lzy.attnd.service;

import com.lzy.attnd.model.SignIn;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Service
@Validated
public interface SignInService {
    @Validated({SignIn.BaseAll.class,SignIn.Openid.class,SignIn.Name.class,SignIn.Cipher.class,SignIn.Location_Struct.class})
    boolean AddSignInRecord(@Valid @NotNull(groups = SignIn.BaseAll.class) SignIn signIn) throws DataAccessException;

    boolean ChkUserHasSignIn(@NotBlank String openid,@NotBlank  String cipher) throws DataAccessException;
}
