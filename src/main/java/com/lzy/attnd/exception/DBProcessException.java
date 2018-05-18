package com.lzy.attnd.exception;

import org.springframework.dao.DataAccessException;

public class DBProcessException extends DataAccessException {
    public DBProcessException(String msg) {
        super(msg);
    }

    public DBProcessException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
