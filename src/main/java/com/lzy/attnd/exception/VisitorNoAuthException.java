package com.lzy.attnd.exception;

public class VisitorNoAuthException extends RuntimeException {
    public VisitorNoAuthException() {
        super();
    }

    public VisitorNoAuthException(String message) {
        super(message);
    }
}
