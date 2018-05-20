package com.lzy.attnd.utils;

import com.lzy.attnd.constant.Code;
import com.lzy.attnd.exception.NoAuthException;
import com.lzy.attnd.exception.SysErrException;
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

    //no auth when session
    @ExceptionHandler(NoAuthException.class)
    public FeedBack NoAuthException(NoAuthException nae){
        return new FeedBack(Code.GLOBAL_NOAUTH,"[no_auth]: "+nae.getMessage());
    }

    //db access failed
    @ExceptionHandler(DataAccessException.class)
    public FeedBack DataAccessFailed(DataAccessException dae){
        return new FeedBack(Code.GLOBAL_DB_ERROR,"[db_failed]: "+dae.getMessage());
    }

    //param invalid in get query
    @ExceptionHandler(ConstraintViolationException.class)
    public FeedBack ParamError(ConstraintViolationException cve){
        return new FeedBack(Code.GLOBAL_PARAM_INVALID,"[param invalid]: "+cve.getMessage());
    }

    //param invalid in post json
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public FeedBack ParamInvalid(MethodArgumentNotValidException mane) {
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
        return new FeedBack(Code.GLOBAL_PARAM_INVALID,"[param invalid]:"+sb.toString());
    }

    //Sys err
    @ExceptionHandler(SysErrException.class)
    public FeedBack SysErr(SysErrException see){
        return FeedBack.SYS_ERROR(see.getMessage());
    }
}
