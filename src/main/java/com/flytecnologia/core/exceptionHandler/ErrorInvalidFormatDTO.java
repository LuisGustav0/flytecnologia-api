package com.flytecnologia.core.exceptionHandler;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ErrorInvalidFormatDTO {

    @JsonProperty
    private String field;

    @JsonProperty
    private String bean;

    @JsonProperty("rejected_value")
    private String rejectedValue;

    @JsonProperty
    private String message;

    @JsonCreator
    private ErrorInvalidFormatDTO() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ErrorInvalidFormatDTO errorDTO = new ErrorInvalidFormatDTO();

        public Builder withBean(String bean) {
            errorDTO.bean = bean;
            return this;
        }

        public Builder withField(String field) {
            errorDTO.field = field;
            return this;
        }

        public Builder withRejectedValue(String rejectedValue) {
            errorDTO.rejectedValue = rejectedValue;
            return this;
        }

        public Builder withMessage(String message) {
            errorDTO.message = message;
            return this;
        }

        public ErrorInvalidFormatDTO build() {
            return errorDTO;
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