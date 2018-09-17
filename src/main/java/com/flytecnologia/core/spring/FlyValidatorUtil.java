package com.flytecnologia.core.spring;

import com.flytecnologia.core.exception.InvalidDataException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import javax.validation.Validation;
import javax.validation.Validator;

public class FlyValidatorUtil {
    private static final Validator javaxValidator = Validation.buildDefaultValidatorFactory().getValidator();
    private static final SpringValidatorAdapter validator = new SpringValidatorAdapter(javaxValidator);

    public static void validate(Object entry) {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(entry, entry.getClass().getName());

        validator.validate(entry, errors);

        if (errors.hasErrors()) {
            throw new InvalidDataException(errors.getAllErrors().toString(), errors);
        }
    }
}
