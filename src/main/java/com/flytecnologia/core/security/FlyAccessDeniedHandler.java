package com.flytecnologia.core.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class FlyAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException exc) {

        addLogInformation(request);

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    private void addLogInformation(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String referrer = request.getHeader("referer");

        String msg = "[AccessDenied] = " + referrer +
                " uri: " + uri +
                " method: " + method +
                " contextPath: " + contextPath;

        msg += " User: ";
        if (auth != null) {
            msg += auth.getName();
        } else {
            msg += "Not found";
        }

        log.warn(msg);
    }
}
