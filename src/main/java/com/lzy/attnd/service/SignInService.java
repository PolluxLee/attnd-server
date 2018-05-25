package com.lzy.attnd.service;

import com.lzy.attnd.constant.Code;
import com.lzy.attnd.model.AttndState;
import com.lzy.attnd.model.SignIn;
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
public interface SignInService {
    @Validated({SignIn.BaseAll.class,SignIn.Openid.class,
            SignIn.Cipher.class,SignIn.Location_Struct.class,SignIn.Dist.class})
    boolean AddSignInRecord(@Valid @NotNull(groups = SignIn.BaseAll.class) SignIn signIn) throws DataAccessException;

    boolean ChkUserHasSignIn(@NotBlank String openid,@NotBlank  String cipher) throws DataAccessException;

    //get signin list
    AttndState[] ChkSignInList(@NotBlank  String cipher, @Min(0) int start, @Min(1) int count, int groupID, @Range(min = Code.SIGNIN_ALL,max = Code.SIGNIN_NOT_EXIST) int signinStatus) throws DataAccessException;

    @Min(0) int CountSignInList(@NotBlank String cipher, @Range(min = Code.SIGNIN_ALL,max = Code.SIGNIN_NOT_EXIST) int signinStatus) throws DataAccessException;

    @Min(0) int CountSignInListWithGroup(@NotBlank String cipher,@Min(1) int groupID, @Range(min = Code.SIGNIN_ALL,max = Code.SIGNIN_NOT_EXIST) int signinStatus) throws DataAccessException;

    boolean UpdSignInSituation(@NotBlank String cipher,@NotBlank String openid, @Range(min = Code.SIGNIN_OK,max = Code.SIGNIN_NOT_EXIST)int statusToUpdate) throws DataAccessException;

    AttndState ChkSignInInfo(@NotBlank String cipher,@NotBlank String openid) throws DataAccessException;
}
