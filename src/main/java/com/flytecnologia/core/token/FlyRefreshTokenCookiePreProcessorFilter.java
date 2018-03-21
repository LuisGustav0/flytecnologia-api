package com.flytecnologia.core.token;

import org.apache.catalina.util.ParameterMap;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.Map;

/*retirar o refresh token do cookie e adicionar na requisição*/
@Profile("oauth-security")
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class FlyRefreshTokenCookiePreProcessorFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        req = addRequestInformation(req);

        //req.setAttribute(FlyMultiTenantConstants.REQUEST_HEADER_ID, getTenantId(req));

        chain.doFilter(req, response);
    }

    private HttpServletRequest addRequestInformation(HttpServletRequest req) {
        if ("/oauth/token".equalsIgnoreCase(req.getRequestURI())
                && "refresh_token".equals(req.getParameter("grant_type"))
                && req.getCookies() != null) {

            for (Cookie cookie : req.getCookies()) {
                if (cookie.getName().equals("refreshToken")) {
                    String refreshToken = cookie.getValue();
                    return new MyServletRequestWrapper(req, refreshToken);
                }
            }
        }

        return req;
    }

   /* private String getTenantId(HttpServletRequest req) {
        String tenant = FlyTokenUserDetails.getCurrentSchemaName();//req.getHeader(FlyMultiTenantConstants.REQUEST_HEADER_ID);

        if (tenant != null) {
            return tenant;
        }

        return FlyMultiTenantConstants.DEFAULT_TENANT_ID;
    }*/

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void destroy() {

    }

    /**
     * Since it is not possible to change the request, a new request is created with the data of the current
     * request plus the refresh token
     **/
    static class MyServletRequestWrapper extends HttpServletRequestWrapper {
        private String refreshToken;

        private MyServletRequestWrapper(HttpServletRequest request,
                                        String refreshToken) {
            super(request);
            this.refreshToken = refreshToken;
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            ParameterMap<String, String[]> map = new ParameterMap<>(getRequest().getParameterMap());
            map.put("refresh_token", new String[]{refreshToken});
            map.setLocked(true);
            return map;
        }
    }
}
