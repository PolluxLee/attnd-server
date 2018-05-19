package com.lzy.attnd.exception;

public class SysErrException extends RuntimeException {
    public SysErrException() {
        super();
    }

    public SysErrException(String message) {
        super(message);
    }
}
