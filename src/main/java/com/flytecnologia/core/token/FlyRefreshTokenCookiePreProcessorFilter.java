package com.flytecnologia.core.token;

import org.apache.catalina.util.ParameterMap;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.Map;

/*remove refresh_token from cookie and add it on the request*/
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

            if (refreshToken != null && refreshToken.length() > 0)
                return new MyServletRequestWrapper(req, refreshToken);
        }

        return req;
    }

    private String extractRefreshToken(HttpServletRequest req) {
        for (Cookie cookie : req.getCookies()) {
            if ("refreshToken".equals(cookie.getName()) &&
                    cookie.getValue() != null &&
                    cookie.getValue().length() > 0)
                return cookie.getValue();
        }

        return null;
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

    @Override
    public void destroy() {
        // do nothing
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        // do nothing
    }
}
