package com.flytecnologia.core.exceptionHandler;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static lombok.AccessLevel.PRIVATE;

@JsonAutoDetect(fieldVisibility = ANY)
@RequiredArgsConstructor(access = PRIVATE)
public class FlyErrorResponse {
    private final int status;
    private final List<ApiError> errors;

    static FlyErrorResponse of(List<ApiError> errors) {
        return new FlyErrorResponse(HttpStatus.BAD_REQUEST.value(), errors);
    }

    static FlyErrorResponse of(ApiError error) {
        return of(Collections.singletonList(error));
    }

    @JsonAutoDetect(fieldVisibility = ANY)
    @RequiredArgsConstructor
    static class ApiError {
        private final String code;
        private final String message;
        private final String msgDev;
    }
}
