package com.flytecnologia.core.spring;

import com.flytecnologia.core.exception.InvalidDataException;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.validation.Validation;
import javax.validation.Validator;
import java.lang.reflect.Method;

public class ValidatorUtil {
    private static final Validator javaxValidator = Validation.buildDefaultValidatorFactory().getValidator();
    private static final SpringValidatorAdapter validator = new SpringValidatorAdapter(javaxValidator);

    public static void validate(Object entry, Class<?> targetClass, String methodName)
            throws MethodArgumentNotValidException {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(entry, entry.getClass().getName());

        validator.validate(entry, errors);

        if (errors.hasErrors()) {

            Method method = null;

            for (Method methodAux : targetClass.getMethods()) {
                if (methodAux.getName().equals(methodName)) {
                    method = methodAux;
                    break;
                }
            }

            if (method != null) {
                MethodParameter methodParameter = new MethodParameter(method, 0);

                throw new MethodArgumentNotValidException(
                        methodParameter,
                        errors);
            } else {
                throw new InvalidDataException(errors.getAllErrors().toString(), errors);
            }
        }
    }
}
