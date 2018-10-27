package com.flytecnologia.core.user.controller;

import com.flytecnologia.core.config.property.FlyAppProperty;
import com.flytecnologia.core.hibernate.multitenancy.FlyTenantThreadLocal;
import com.flytecnologia.core.user.FlyUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "login")
public class FlyLoginController {

    private FlyUserService service;
    private FlyAppProperty flyAppProperty;

    @Autowired
    public FlyLoginController(FlyUserService service,
                              FlyAppProperty flyAppProperty) {
        this.service = service;
        this.flyAppProperty = flyAppProperty;
    }

    @DeleteMapping("/revoke")
    public void revoke(HttpServletRequest req, HttpServletResponse resp) {
        String path = req.getContextPath() + "oauth/token";

        _revoke("refreshToken1", path, resp);
        _revoke("refreshToken2", path, resp);
        _revoke("refreshToken3", path, resp);
        _revoke("refreshToken4", path, resp);

        resp.setStatus(HttpStatus.NO_CONTENT.value());
    }

    private void _revoke(String cookieName, String path, HttpServletResponse resp) {
        FlyTenantThreadLocal.clear();

        Cookie cookie = new Cookie(cookieName, null);
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
