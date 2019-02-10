package com.flytecnologia.core.exceptionHandler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.flytecnologia.core.config.property.FlyAppProperty;
import com.flytecnologia.core.exception.BE;
import com.flytecnologia.core.exception.BusinessException;
import com.flytecnologia.core.exception.InvalidDataException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
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
import java.util.List;

import static com.flytecnologia.core.exceptionHandler.FlyErrorResponse.ApiError;
import static java.util.stream.Collectors.toList;

@Slf4j
@Component
@ControllerAdvice
public class FlyExceptionHandler extends ResponseEntityExceptionHandler {
    private static final String NO_MESSSAGE_AVAILABLE = "No message available";
    private MessageSource messageSource;
    private FlyAppProperty flyAppProperty;
    private FieldError fieldError;

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
            return invalidFormatExceptionHandler((InvalidFormatException) ex.getCause());
        }

        final ApiError apiError = toApiError("message.invalid", ex);

        return ResponseEntity.badRequest().body(FlyErrorResponse.of(apiError));
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<Object> invalidFormatExceptionHandler(InvalidFormatException e) {
        final List<ApiError> apiErrors = toApiErrors(invalidFormatExceptiontoErrors(e), e.getMessage());

        return ResponseEntity.badRequest().body(FlyErrorResponse.of(apiErrors));
    }

    private List<FlyErrorInvalidFormat> invalidFormatExceptiontoErrors(
            InvalidFormatException e) {
        return e.getPath()
                .stream()
                .map(x -> FlyErrorInvalidFormat.builder()
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
        final List<ApiError> apiErrors = toApiErrors(ex.getBindingResult());

        return ResponseEntity.badRequest().body(FlyErrorResponse.of(apiErrors));
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        final String message = String.format("%s parameter is missing", ex.getParameterName());
        final ApiError apiError = toApiError(message, ex);

        return ResponseEntity.badRequest().body(FlyErrorResponse.of(apiError));
    }

    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<FlyErrorResponse> handleInvalidDataException(InvalidDataException ex) {
        final List<ApiError> apiErrors = toApiErrors(ex.getBindingResult());

        return getBodyBadRequest(apiErrors);
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<FlyErrorResponse> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex) {
        final ApiError apiError = toApiError("resource.not-found", ex);

        return getBodyBadRequest(apiError);
    }


    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<FlyErrorResponse> handleInvalidParameterException(InvalidParameterException ex) {
        ApiError apiError;

        if (ex.getMessage().contains(" ")) {
            apiError = toApiError("resource.invalid-parameter", ex);
        } else {
            apiError = toApiError(ex.getMessage(), ex);
        }

        return getBodyBadRequest(apiError);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<FlyErrorResponse> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex) {

        String fieldError = "resource.operation-not-allowed";

        if (ex.getCause() instanceof ConstraintViolationException) {
            fieldError = ((ConstraintViolationException) ex.getCause()).getConstraintName();
        }

        final ApiError apiError = toApiError(fieldError, ex);

        return getBodyBadRequest(apiError);
    }

    @ExceptionHandler({BusinessException.class, BE.class})
    public ResponseEntity<FlyErrorResponse> handleBusinessException(BusinessException ex) {
        String msg = ex.getMessage();

        ApiError apiError;
        if(msg != null && !msg.contains(" ")) {
            apiError = toApiError(ex.getMessage(), ex);
        } else {
            apiError = new ApiError("businessException", ex.getMessage(), getDevMessage(ex));
        }

        if (flyAppProperty.getApp().isDebug()) {
            log.error(ex.getMessage());
        }

        return getBodyBadRequest(apiError);
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseEntity<FlyErrorResponse> handleConstraintViolationException(RuntimeException ex) {
        final String fieldError = ((ConstraintViolationException) ex.getCause()).getConstraintName();

        final ApiError apiError = toApiError(fieldError, ex);

        return getBodyBadRequest(apiError);
    }

    private ApiError toApiError(String fieldError, Exception ex, Object... args) {
        return new ApiError(fieldError, getMessage(fieldError, args), getDevMessage(ex));
    }

    private List<ApiError> toApiErrors(List<FlyErrorInvalidFormat> errorsDto,
                                       String errorMessage) {
        final List<ApiError> apiErrors = new ArrayList<>();

        for (FlyErrorInvalidFormat error : errorsDto) {
            String errorStr = error.getBean();
            errorStr = errorStr.substring(0, 1).toLowerCase() + errorStr.substring(1);
            errorStr += "." + error.getField();

            final String message = getMessage(errorStr, error.getRejectedValue());

            String messageInvalidField = getMessage("resource.invalidFieldValueFormat", "Field '%s' with invalid format. Value: '%s'");

            messageInvalidField = String.format(messageInvalidField, message, error.getRejectedValue());

            apiErrors.add(new ApiError("invalidFieldValueFormat", messageInvalidField, errorMessage));
        }

        return apiErrors;
    }

    private List<ApiError> toApiErrors(BindingResult bindingResult) {
        final List<ApiError> apiErrors = new ArrayList<>();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            final String message = getMessage(fieldError);

            apiErrors.add(new ApiError(fieldError.getField(), message, getDevMessage(fieldError)));
        }

        return apiErrors;
    }

    private String getMessage(FieldError fieldError) {
        try {
            return messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());
        } catch (Exception ex) {
            return NO_MESSSAGE_AVAILABLE;
        }
    }

    private String getMessage(String field, Object... args) {
        try {
             return messageSource.getMessage(field, args, LocaleContextHolder.getLocale());
        } catch (Exception ex) {
            return NO_MESSSAGE_AVAILABLE;
        }
    }

    private String getMessage(String field, String defaultMessage, Object... args) {
        String message;
        try {
            message = messageSource.getMessage(field, args, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException e) {
            log.error("Could not find any message for {} code under {} locale", field, LocaleContextHolder.getLocale());
            message = defaultMessage;
        }

        return message;
    }

    private String getDevMessage(Exception ex) {
        if (!flyAppProperty.getApp().isDebug()) {
            return null;
        }

        return ex != null ? ExceptionUtils.getRootCauseMessage(ex) : null;
    }

    private String getDevMessage(FieldError fieldError, Object... args) {
        if (!flyAppProperty.getApp().isDebug()) {
            return null;
        }

        return fieldError.toString();
    }

    private ResponseEntity<FlyErrorResponse> getBodyBadRequest(List<ApiError> apiErrors) {
        return ResponseEntity.badRequest().body(FlyErrorResponse.of(apiErrors));
    }

    private ResponseEntity<FlyErrorResponse> getBodyBadRequest(ApiError apiError) {
        return ResponseEntity.badRequest().body(FlyErrorResponse.of(apiError));
    }
}
