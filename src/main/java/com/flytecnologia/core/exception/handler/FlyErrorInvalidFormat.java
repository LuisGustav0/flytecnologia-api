package com.flytecnologia.core.exception.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
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
}