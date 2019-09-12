package com.flytecnologia.core.exception;

import lombok.Getter;
import org.springframework.validation.BindingResult;

@Getter
public class InvalidDataException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final BindingResult bindingResult;

    public InvalidDataException(String msg, BindingResult bindingResult) {
        super(msg);
        this.bindingResult = bindingResult;
    }
}
