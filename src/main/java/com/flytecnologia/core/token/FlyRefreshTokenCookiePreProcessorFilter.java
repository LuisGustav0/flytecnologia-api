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
    private static final String LOGIN_REST_URL = "/oauth/token";


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        final HttpServletRequest req = addRequestInformation((HttpServletRequest) request);

        chain.doFilter(req, response);
    }

    private HttpServletRequest addRequestInformation(HttpServletRequest req) {
        if (LOGIN_REST_URL.equalsIgnoreCase(req.getRequestURI())
                && "refresh_token".equals(req.getParameter("grant_type"))
                && req.getCookies() != null) {

            String refreshToken = extractRefreshToken(req);

            if (refreshToken.length() > 0)
                return new MyServletRequestWrapper(req, refreshToken);
        }

        return req;
    }

    private String extractRefreshToken(HttpServletRequest req) {
        String refreshToken1 = "";
        String refreshToken2 = "";
        String refreshToken3 = "";
        String refreshToken4 = "";

        for (Cookie cookie : req.getCookies()) {
            switch (cookie.getName()) {
                case "refreshToken1":
                    if (cookie.getValue() != null && cookie.getValue().length() > 0)
                        refreshToken1 = cookie.getValue();
                    break;
                case "refreshToken2":
                    if (cookie.getValue() != null && cookie.getValue().length() > 0)
                        refreshToken2 = cookie.getValue();
                    break;
                case "refreshToken3":
                    if (cookie.getValue() != null && cookie.getValue().length() > 0)
                        refreshToken3 = cookie.getValue();
                    break;
                case "refreshToken4":
                    if (cookie.getValue() != null && cookie.getValue().length() > 0)
                        refreshToken4 = cookie.getValue();
                    break;
            }
        }

        return refreshToken1 + refreshToken2 + refreshToken3 + refreshToken4;
    }

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
            final ParameterMap<String, String[]> map = new ParameterMap<>(getRequest().getParameterMap());
            map.put("refresh_token", new String[]{refreshToken});
            map.setLocked(true);
            return map;
        }
    }
}
