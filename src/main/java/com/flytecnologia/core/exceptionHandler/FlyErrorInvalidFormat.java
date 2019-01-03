package com.flytecnologia.core.exceptionHandler;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FlyErrorInvalidFormat {

    @JsonProperty
    private String field;

    @JsonProperty
    private String bean;

    @JsonProperty("rejected_value")
    private String rejectedValue;

    @JsonProperty
    private String message;

    @JsonCreator
    private FlyErrorInvalidFormat() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private FlyErrorInvalidFormat error = new FlyErrorInvalidFormat();

        Builder withBean(String bean) {
            error.bean = bean;
            return this;
        }

        Builder withField(String field) {
            error.field = field;
            return this;
        }

        Builder withRejectedValue(String rejectedValue) {
            error.rejectedValue = rejectedValue;
            return this;
        }

        Builder withMessage(String message) {
            error.message = message;
            return this;
        }

        public FlyErrorInvalidFormat build() {
            return error;
        }
    }

    public String getField() {
        return field;
    }

    public String getBean() {
        return bean;
    }

    public String getRejectedValue() {
        return rejectedValue;
    }

    public String getMessage() {
        return message;
    }
}