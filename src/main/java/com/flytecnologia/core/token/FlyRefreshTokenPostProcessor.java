package com.flytecnologia.core.token;

import com.flytecnologia.core.config.property.FlyAppProperty;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*

After create the refresh token, before returning the response, go through here.

Objective: Remove the refresh token from the response and put in a cookie so that it is not accessed by javascript

*/
@Profile("oauth-security")
@ControllerAdvice
public class FlyRefreshTokenPostProcessor implements ResponseBodyAdvice<OAuth2AccessToken> {

    @Autowired
    private FlyAppProperty flyAppProperty;

    /*only executes the beforeBodyWrite method if it returns true*/
    @Override
    public boolean supports(@NonNull MethodParameter returnType,
                            @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.getMethod().getName().equals("postAccessToken");
    }

    @Override
    public OAuth2AccessToken beforeBodyWrite(OAuth2AccessToken body, MethodParameter returnType,
                                             MediaType selectedContentType,
                                             Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                             ServerHttpRequest request, ServerHttpResponse response) {

        final HttpServletRequest req = ((ServletServerHttpRequest) request).getServletRequest();
        final HttpServletResponse resp = ((ServletServerHttpResponse) response).getServletResponse();
        final DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) body;
        final String refreshToken = body.getRefreshToken().getValue();

        addRefreshTokenInCookie(refreshToken, req, resp);
        deleteRefreshTokenFromBody(token);

        return body;
    }

    private void deleteRefreshTokenFromBody(DefaultOAuth2AccessToken token) {
        token.setRefreshToken(null);
    }

    private void addRefreshTokenInCookie(String refreshToken, HttpServletRequest req, HttpServletResponse resp) {
        final String path = req.getContextPath() + "/oauth/token";

        final Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(flyAppProperty.getSecurity().isEnableHttps());
        cookie.setPath(path);
        cookie.setMaxAge(259200); //expiration time
        resp.addCookie(cookie);
    }
}
