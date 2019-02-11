package com.flytecnologia.core.recaptcha;

import com.flytecnologia.core.config.property.FlyAppProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

@Service
@Slf4j
public class FlyReCaptchaService {
    private static final String RE_CAPTCHA_RESPONSE = "reCaptchaResponse";

    private RestOperations restTemplate;
    private FlyReCaptchaSettings reCaptchaSettings;
    private FlyAppProperty appProperty;

    public FlyReCaptchaService(RestOperations restTemplate,
                               FlyReCaptchaSettings reCaptchaSettings,
                               FlyAppProperty appProperty) {
        this.restTemplate = restTemplate;
        this.reCaptchaSettings = reCaptchaSettings;
        this.appProperty = appProperty;
    }


    public boolean isValidRecaptcha(HttpServletRequest request) {
        if (appProperty.getApp().isDebug())
            return true;

        String reCaptchaResponse = request.getParameter(RE_CAPTCHA_RESPONSE);

        if (reCaptchaResponse == null || reCaptchaResponse.trim().length() == 0) {
            return reCaptchaSettings.getSecret() == null;//optional use
        }

        URI verifyUri = URI.create(String.format(
                reCaptchaSettings.getUrl() + "?secret=%s&response=%s&remoteip=%s",
                reCaptchaSettings.getSecret(),
                reCaptchaResponse,
                request.getRemoteAddr()
        ));

        try {
            FlyReCaptchaResponse response = restTemplate.getForObject(verifyUri, FlyReCaptchaResponse.class);
            return response != null && response.isSuccess();
        } catch (Exception ignored) {
            ignored.printStackTrace();
            //log.error("", ignored);
            // ignore when google services are not available
            // maybe add some sort of logging or trigger that'll alert the administrator
        }

        return true;
    }

}