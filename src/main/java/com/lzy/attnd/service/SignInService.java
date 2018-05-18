package com.lzy.attnd.service;

import com.lzy.attnd.model.SignIn;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Service
@Validated
public interface SignInService {
    //TODO
    @Validated()
    boolean AddSignInRecord(@Valid @NotNull SignIn signIn) throws DataAccessException;
}
