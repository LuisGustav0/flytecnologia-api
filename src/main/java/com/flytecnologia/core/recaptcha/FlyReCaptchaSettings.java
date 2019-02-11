package com.flytecnologia.core.recaptcha;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "google.recaptcha")
public class FlyReCaptchaSettings {
    private String url;
    private String key;
    private String secret;
}
