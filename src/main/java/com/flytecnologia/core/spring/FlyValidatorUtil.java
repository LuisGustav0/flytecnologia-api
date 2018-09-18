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
        String entityName = entry.getClass().getSimpleName();
        entityName = entityName.substring(0,1).toLowerCase().concat(entityName.substring(1));

        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(entry, entityName);

        validator.validate(entry, errors);

        if (errors.hasErrors()) {
            throw new InvalidDataException(errors.getAllErrors().toString(), errors);
        }
    }
}
