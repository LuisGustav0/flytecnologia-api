package com.flytecnologia.core.exception;

import org.springframework.validation.BindingResult;

public class InvalidDataException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private BindingResult bindingResult;

    public InvalidDataException(String msg, BindingResult bindingResult) {
        super(msg);
        setBindingResult(bindingResult);
    }

    public BindingResult getBindingResult() {
        return bindingResult;
    }

    public void setBindingResult(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }
}
