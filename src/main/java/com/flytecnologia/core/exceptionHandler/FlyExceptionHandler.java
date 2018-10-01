package com.flytecnologia.core.exceptionHandler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.flytecnologia.core.config.property.FlyAppProperty;
import com.flytecnologia.core.exception.BE;
import com.flytecnologia.core.exception.BusinessException;
import com.flytecnologia.core.exception.InvalidDataException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
@ControllerAdvice
public class FlyExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger logger = LogManager.getLogger(FlyExceptionHandler.class);

    private MessageSource messageSource;
    private FlyAppProperty flyAppProperty;

    @Autowired
    public FlyExceptionHandler(MessageSource messageSource,
                               FlyAppProperty flyAppProperty) {
        this.messageSource = messageSource;
        this.flyAppProperty = flyAppProperty;
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {
        ex.printStackTrace();

        if (ex.getCause() instanceof InvalidFormatException) {
            return invalidFormatExceptionHandler((InvalidFormatException) ex.getCause(), headers, request);
        }

        List<Error> errors = getListOfErros("message.invalid", ex);
        return handleExceptionInternal(ex, errors, headers, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<Object> invalidFormatExceptionHandler(InvalidFormatException e,
                                                                HttpHeaders headers, WebRequest request) {
        List<Error> errors = createListOfErros(invalidFormatExceptiontoErrors(e), e.getMessage());
        return handleExceptionInternal(e, errors, headers, HttpStatus.BAD_REQUEST, request);
    }

    private List<ErrorInvalidFormatDTO> invalidFormatExceptiontoErrors(InvalidFormatException e) {
        return e.getPath()
                .stream()
                .map(x -> ErrorInvalidFormatDTO.builder()
                        .withField(x.getFieldName())
                        .withBean(x.getFrom().getClass().getSimpleName())
                        .withMessage("field format error")
                        .withRejectedValue(e.getValue().toString())
                        .build())
                .collect(toList());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {
        List<Error> errors = createListOfErros(ex.getBindingResult());
        return handleExceptionInternal(ex, errors, headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        String error = String.format("%s parameter is missing", ex.getParameterName());

        List<Error> errors = getListOfErros(error, ex);
        return handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<Object> handleInvalidDataException(InvalidDataException ex,
                                                             WebRequest request) {
        List<Error> errors = createListOfErros(ex.getBindingResult());
        return handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<Object> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex,
                                                                       WebRequest request) {
        List<Error> errors = getListOfErros("resource.not-found", ex);
        return handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }


    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<Object> handleInvalidParameterException(InvalidParameterException ex,
                                                                  WebRequest request) {
        List<Error> errors;

        if (ex.getMessage().contains(" ")) {
            errors = getListOfErros("resource.invalid-parameter", ex);
        } else {
            errors = getListOfErros(ex.getMessage(), ex);
        }

        return handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex,
                                                                        WebRequest request) {

        String fieldError = "resource.operation-not-allowed";

        if (ex.getCause() instanceof ConstraintViolationException) {
            fieldError = ((ConstraintViolationException) ex.getCause()).getConstraintName();
        }

        List<Error> errors = getListOfErros(fieldError, ex);
        return handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({BusinessException.class, BE.class})
    public ResponseEntity<Object> handleBusinessException(BusinessException ex,
                                                          WebRequest request) {
        List<Error> errors = getListOfErros(ex.getMessage(), null);

        if (flyAppProperty.getApp().isDebug()) {
            logger.error(ex.getMessage());
        }

        return handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolationException(RuntimeException ex, WebRequest request) {
        String fieldError = ((ConstraintViolationException) ex.getCause()).getConstraintName();

        List<Error> errors = getListOfErros(fieldError, ex);
        return handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    private List<Error> getListOfErros(String fieldError, Exception ex) {
        String msgUser = getMessage(fieldError);
        String msgDev = ex != null ? ExceptionUtils.getRootCauseMessage(ex) : "";
        return Arrays.asList(new Error(msgUser, msgDev));
    }

    private String getMessage(String field) {
        try {
            return messageSource.getMessage(field, null, LocaleContextHolder.getLocale());
        } catch (Exception ex) {
            return field;
        }

    }

    private List<Error> createListOfErros(List<ErrorInvalidFormatDTO> errorsDto, String errorMessage) {
        List<Error> errors = new ArrayList<>();

        for (ErrorInvalidFormatDTO error : errorsDto) {
            String errorStr = error.getBean();
            errorStr = errorStr.substring(0, 1).toLowerCase() + errorStr.substring(1);
            errorStr += "." + error.getField();

            String field = messageSource.getMessage(errorStr, new Object[]{error.getRejectedValue()}, LocaleContextHolder.getLocale());
            String errorFormat = messageSource.getMessage("resource.invalidFieldValueFormat", new Object[]{}, LocaleContextHolder.getLocale());

            if (!errorFormat.contains(" ")) {
                errorFormat = "Field '%s' with invalid format. Value: '%s'";
            }

            errorFormat = String.format(errorFormat, field, error.getRejectedValue());

            errors.add(new Error(errorFormat, errorMessage));
        }

        return errors;
    }

    private List<Error> createListOfErros(BindingResult bindingResult) {
        List<Error> errors = new ArrayList<>();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            String msgUser = messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());
            String msgDev = fieldError.toString();
            errors.add(new Error(msgUser, msgDev));
        }

        return errors;
    }

    public static class Error {
        private String msgUser;
        private String msgDev;

        private Error(String msgUser, String msgDev) {
            this.msgUser = msgUser;
            this.msgDev = msgDev;
        }

        public String getMsgUser() {
            return msgUser;
        }

        public String getMsgDev() {
            return msgDev;
        }

    }
}
