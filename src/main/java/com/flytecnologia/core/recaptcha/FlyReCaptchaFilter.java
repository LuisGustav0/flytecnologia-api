package com.flytecnologia.core.recaptcha;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Profile("oauth-security")
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class FlyReCaptchaFilter implements Filter {
    private static final String LOGIN_REST_URL = "/oauth/token";
    private static final String RESET_REST_URL = "/login/reset-password";
    private static final String SEND_NEW_PASSWORD_REST_URL = "/login/send-new-password";

    @Autowired
    private FlyReCaptchaService reCaptchaService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        final HttpServletRequest req = (HttpServletRequest) request;

        if (isReCaptchaValid(req)) {
            chain.doFilter(req, response);
        } else {
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "The ReCaptcha is not valid.");
        }
    }

    private boolean isReCaptchaValid(final HttpServletRequest req) {
        return isReCaptchaValidFromLogin(req) &&
                isReCaptchaValidFromResetPassword(req) &&
                isReCaptchaValidFromSendNewPassword(req);
    }

    private boolean isReCaptchaValidFromLogin(final HttpServletRequest req) {
        if (LOGIN_REST_URL.equalsIgnoreCase(req.getRequestURI())) {
            return reCaptchaService.isValidRecaptcha(req);
        }

        return true;
    }

    private boolean isReCaptchaValidFromResetPassword(final HttpServletRequest req) {
        if (RESET_REST_URL.equalsIgnoreCase(req.getRequestURI())) {
            return reCaptchaService.isValidRecaptcha(req);
        }

        return true;
    }

    private boolean isReCaptchaValidFromSendNewPassword(final HttpServletRequest req) {
        if (SEND_NEW_PASSWORD_REST_URL.equalsIgnoreCase(req.getRequestURI())) {
            return reCaptchaService.isValidRecaptcha(req);
        }

        return true;
    }
}
