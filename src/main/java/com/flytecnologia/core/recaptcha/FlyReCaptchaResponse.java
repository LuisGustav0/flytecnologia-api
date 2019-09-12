package com.flytecnologia.core.recaptcha;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
        "success",
        "challenge_ts",
        "hostname",
        "error-codes"
})
@Getter
@Setter
public class FlyReCaptchaResponse {

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("challenge_ts")
    private Date challengeTs;

    @JsonProperty("hostname")
    private String hostname;

    public boolean isSuccess() {
        return success;
    }
}