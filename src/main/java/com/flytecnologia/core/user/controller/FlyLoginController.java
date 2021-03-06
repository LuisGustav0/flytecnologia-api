package com.flytecnologia.core.user.controller;

import com.flytecnologia.core.config.property.FlyAppProperty;
import com.flytecnologia.core.hibernate.multitenancy.FlyTenantThreadLocal;
import com.flytecnologia.core.user.FlyUserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "login")
@AllArgsConstructor
public class FlyLoginController {
    private FlyUserService service;
    private FlyAppProperty flyAppProperty;

    @DeleteMapping("/revoke")
    public void revoke(HttpServletRequest req, HttpServletResponse resp) {
        final String path = req.getContextPath() + "oauth/token";

        revoke("refreshToken", path, resp);

        resp.setStatus(HttpStatus.NO_CONTENT.value());

        FlyTenantThreadLocal.remove();
    }

    private void revoke(String cookieName, String path, HttpServletResponse resp) {
        final Cookie cookie = new Cookie(cookieName, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(flyAppProperty.getSecurity().isEnableHttps());
        cookie.setPath(path);
        cookie.setMaxAge(0);

        resp.addCookie(cookie);
    }

    @PostMapping("/send-new-password")
    public ResponseEntity<Void> sendNewPassword(@RequestParam("username") String username) {
        service.sendNewPassword(username);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(
            @RequestParam("changePasswordKey") String changePasswordKey,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword) {
        service.resetPassword(changePasswordKey, password, confirmPassword);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
