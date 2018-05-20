package com.lzy.attnd.exception;

public class NoAuthException extends RuntimeException {
    public NoAuthException() {
        super();
    }

    public NoAuthException(String message) {
        super(message);
    }
}
