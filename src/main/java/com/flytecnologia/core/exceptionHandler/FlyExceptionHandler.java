package com.flytecnologia.core.exceptionHandler;

import com.flytecnologia.core.exception.BusinessException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ControllerAdvice
public class FlyExceptionHandler extends ResponseEntityExceptionHandler {

    private MessageSource messageSource;

    @Autowired
    public FlyExceptionHandler(MessageSource messageSource){
        this.messageSource = messageSource;
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {
        List<Error> erros = getListOfErros("message.invalid", ex);
        return handleExceptionInternal(ex, erros, headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {
        List<Error> erros = createListOfErros(ex.getBindingResult());
        return handleExceptionInternal(ex, erros, headers, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({EmptyResultDataAccessException.class})
    public ResponseEntity<Object> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex,
                                                                       WebRequest request) {
        List<Error> erros = getListOfErros("resource.not-found", ex);
        return handleExceptionInternal(ex, erros, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }


    @ExceptionHandler({InvalidParameterException.class})
    public ResponseEntity<Object> handleDataIntegrityViolationException(InvalidParameterException ex,
                                                                        WebRequest request) {
        List<Error> errors = null;

        if(ex.getMessage().contains(" ")){
            errors = getListOfErros("resource.invalid-parameter", ex);
        }
        else {
            errors = getListOfErros(ex.getMessage(), ex);
        }

        return handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex,
                                                                        WebRequest request) {
        List<Error> errors = getListOfErros("resource.operation-not-allowed", ex);
        return handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({BusinessException.class})
    public ResponseEntity<Object> handleDataIntegrityViolationException(BusinessException ex,
                                                                        WebRequest request) {
        List<Error> errors = getListOfErros(ex.getMessage(), null);
        return handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    private List<Error> getListOfErros(String fieldError, Exception ex){
        String msgUser = getMessage(fieldError);
        String msgDev = ex != null ? ExceptionUtils.getRootCauseMessage(ex) : "";
        return Arrays.asList(new Error(msgUser, msgDev));
    }

    private String getMessage(String field){
        return messageSource.getMessage(field, null, LocaleContextHolder.getLocale());
    }

    private List<Error> createListOfErros(BindingResult bindingResult) {
        List<Error> erros = new ArrayList<>();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            String msgUser = messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());
            String msgDev = fieldError.toString();
            erros.add(new Error(msgUser, msgDev));
        }

        return erros;
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
