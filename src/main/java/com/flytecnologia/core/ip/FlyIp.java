package com.flytecnologia.core.ip;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class FlyIp {
    private HttpServletRequest request;

    public FlyIp(HttpServletRequest request){
        this.request = request;
    }

    public String getClientIp() {
        String ip = "";

        if (request != null) {
            ip = request.getHeader("X-Forwarded-For");
            
            if (isEmpty(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }

            if (isEmpty(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }

            if (isEmpty(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }

            if (isEmpty(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED");
            }

            if (isEmpty(ip)) {
                ip = request.getHeader("HTTP_X_CLUSTER_CLIENT_IP");
            }

            if (isEmpty(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }

            if (isEmpty(ip)) {
                ip = request.getHeader("HTTP_FORWARDED_FOR");
            }

            if (isEmpty(ip)) {
                ip = request.getHeader("HTTP_FORWARDED");
            }

            if (isEmpty(ip)) {
                ip = request.getHeader("HTTP_VIA");
            }

            if (isEmpty(ip)) {
                ip = request.getHeader("REMOTE_ADDR");
            }

            if (isEmpty(ip)) {
                ip = request.getRemoteAddr();
            }
        }

        return ip;
    }

    private boolean isEmpty(String ip) {
        return StringUtils.isEmpty(ip) || ip.equalsIgnoreCase("unknown");
    }
}
