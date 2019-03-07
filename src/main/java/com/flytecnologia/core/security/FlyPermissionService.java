package com.flytecnologia.core.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;

public class FlyPermissionService {
    public static boolean hasAnyPermission(String... roles) {
        if (roles == null || roles.length == 0) {
            return false;
        }

        final SecurityContext securityContext = SecurityContextHolder.getContext();
        final Authentication authentication = securityContext.getAuthentication();

        if (authentication != null) {
            List<String> rolesList = Arrays.asList(roles);

            return authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> rolesList.contains(grantedAuthority.getAuthority()));
        }
        return false;
    }
}
