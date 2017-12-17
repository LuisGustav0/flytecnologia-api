package com.flytecnologia.core.exception;

public class BusinessException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public BusinessException(String msgUser) {
        super(msgUser);
    }
}
