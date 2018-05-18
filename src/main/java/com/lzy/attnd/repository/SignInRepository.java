package com.lzy.attnd.repository;

import com.lzy.attnd.model.SignIn;
import com.lzy.attnd.service.SignInService;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;


@Repository
public class SignInRepository implements SignInService {
    @Override
    public boolean AddSignInRecord(SignIn signIn) throws DataAccessException {
        return false;
    }
}
