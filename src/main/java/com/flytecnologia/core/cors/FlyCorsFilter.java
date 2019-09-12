package com.flytecnologia.core.cors;

import com.flytecnologia.core.config.property.FlyAppProperty;
import lombok.AllArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@AllArgsConstructor
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class FlyCorsFilter implements Filter {
    private FlyAppProperty flyAppProperty;

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) resp;
        String allowOrigin = flyAppProperty.getSecurity().getAllowOrigin();

        if (allowOrigin == null || allowOrigin.trim().length() == 0 || "any".equals(allowOrigin)) {
            allowOrigin = request.getHeader("Origin");
        }

        response.addHeader("Access-Control-Allow-Origin", allowOrigin);
        response.addHeader("Access-Control-Allow-Credentials", "true");

        if ("OPTIONS".equals(request.getMethod())) {
            response.addHeader("Access-Control-Allow-Methods", "POST, GET, DELETE, PUT, OPTIONS");
            response.addHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept");
            response.addHeader("Access-Control-Max-Age", "3600");

            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            chain.doFilter(req, resp);
        }

    }
}
