package com.flytecnologia.core.token;

import com.flytecnologia.core.hibernate.multitenancy.FlyMultiTenantConstants;
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

        req = defineRefreshCookie(req);

        defineTenantId(req);

        chain.doFilter(req, response);
    }

    private HttpServletRequest defineRefreshCookie(HttpServletRequest req) {
        if ("/oauth/token".equalsIgnoreCase(req.getRequestURI())
                && "refresh_token".equals(req.getParameter("grant_type"))
                && req.getCookies() != null) {

            for (Cookie cookie : req.getCookies()) {
                if (cookie.getName().equals("refreshToken")) {
                    String refreshToken = cookie.getValue();
                    req = new MyServletRequestWrapper(req, refreshToken);
                }
            }
        }
        return req;
    }

    private void defineTenantId(HttpServletRequest req) {
        String tenant = req.getHeader(FlyMultiTenantConstants.REQUEST_HEADER_ID);

        if (tenant != null) {
            req.setAttribute(FlyMultiTenantConstants.REQUEST_HEADER_ID, tenant);
        } else {
            req.setAttribute(FlyMultiTenantConstants.REQUEST_HEADER_ID, FlyMultiTenantConstants.DEFAULT_TENANT_ID);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void destroy() {

    }

    /*Como não é possível alterar a requisição, uma nova requisição é criada com os dados da requisição
    * atual mais o refresh token*/
    static class MyServletRequestWrapper extends HttpServletRequestWrapper {
        private String refreshToken;

        private MyServletRequestWrapper(HttpServletRequest request, String refreshToken) {
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
