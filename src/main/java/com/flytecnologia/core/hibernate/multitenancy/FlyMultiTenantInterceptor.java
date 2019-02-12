package com.flytecnologia.core.hibernate.multitenancy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class FlyMultiTenantInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private TokenStore tokenStore;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader(FlyMultiTenantConstants.REQUEST_TOKEN_HEADER);

        if (token != null && token.startsWith("Bearer")) {
            OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(token.substring(7));

            if (oAuth2AccessToken != null) {
                Map<String, Object> information = oAuth2AccessToken.getAdditionalInformation();

                String tenant = (String) information.get(FlyMultiTenantConstants.REQUEST_HEADER_ID);
                Integer userId = (Integer) information.get(FlyMultiTenantConstants.REQUEST_HEADER_USER_ID);

                if (tenant != null) {
                    FlyTenantThreadLocal.setTenant(FlyMultiTenantConstants.DEFAULT_TENANT_SUFFIX + tenant);
                }

                if (userId != null) {
                    FlyTenantThreadLocal.setUserId((long) userId);
                }
            }
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        FlyTenantThreadLocal.remove();
    }
}
