package com.lzy.attnd.utils;

import com.lzy.attnd.constant.Code;
import com.lzy.attnd.exception.NoAuthException;
import com.lzy.attnd.exception.SysErrException;
import com.lzy.attnd.exception.VisitorNoAuthException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    //user invalid
    @ExceptionHandler(VisitorNoAuthException.class)
    public FB VisitorNoAuth(VisitorNoAuthException vnae){
        return new FB(Code.GLOBAL_USER_NOT_EXIST,"[visitor no auth]: "+vnae.getMessage());
    }

    //no auth when session
    @ExceptionHandler(NoAuthException.class)
    public FB NoAuthException(NoAuthException nae){
        return new FB(Code.GLOBAL_NOAUTH,"[no_auth]: "+nae.getMessage());
    }

    //db access failed
    @ExceptionHandler(DataAccessException.class)
    public FB DataAccessFailed(DataAccessException dae){
        return new FB(Code.GLOBAL_DB_ERROR,"[db_failed]: "+dae.getMessage());
    }

    //param invalid in get query
    @ExceptionHandler(ConstraintViolationException.class)
    public FB ParamError(ConstraintViolationException cve){
        return new FB(Code.GLOBAL_PARAM_INVALID,"[param invalid]: "+cve.getMessage());
    }

    //param invalid in post json
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public FB ParamInvalid(MethodArgumentNotValidException mane) {
        List<ObjectError> allParamError = mane.getBindingResult().getAllErrors();
        StringBuilder sb = new StringBuilder(" ");
        for (int i = 0; i < allParamError.size(); i++) {
            FieldError fieldError;
            try{
                fieldError = (FieldError)(allParamError.get(i));
            }catch (ClassCastException cce){
                logger.warn("allParamError cast to FieldError failed: "+cce.getMessage());
                break;
            }
            sb.append(i+". ");
            sb.append(fieldError.getField());
            sb.append(" ");
            sb.append(fieldError.getDefaultMessage());
            sb.append(" ");
        }
        return new FB(Code.GLOBAL_PARAM_INVALID,"[param invalid]:"+sb.toString());
    }

    //Sys err
    @ExceptionHandler(SysErrException.class)
    public FB SysErr(SysErrException see){
        return FB.SYS_ERROR(see.getMessage());
    }
}
