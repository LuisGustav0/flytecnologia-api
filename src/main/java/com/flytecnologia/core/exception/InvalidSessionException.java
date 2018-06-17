package com.flytecnologia.core.exception;

public class InvalidSessionException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public InvalidSessionException() {
        super("InvalidSessionException");
    }
}
