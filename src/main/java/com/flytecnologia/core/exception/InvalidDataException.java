package com.flytecnologia.core.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.BindingResult;

@Getter
@Setter
public class InvalidDataException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private BindingResult bindingResult;

    public InvalidDataException(String msg, BindingResult bindingResult) {
        super(msg);
        setBindingResult(bindingResult);
    }
}
