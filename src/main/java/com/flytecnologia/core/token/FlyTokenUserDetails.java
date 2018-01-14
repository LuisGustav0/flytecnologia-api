package com.flytecnologia.core.token;

import com.flytecnologia.core.user.FlyUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Map;
import java.util.Optional;

public class FlyTokenUserDetails {
    private static Optional<Authentication> getAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
    }

    public static Long getCurrentUserId() {
        Optional<Authentication> authentication = getAuthentication();

        Optional<Long> opt = authentication.map(a -> {
            if(!(a.getDetails() instanceof OAuth2AuthenticationDetails))
                return null;

            OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) a.getDetails();
            Integer userId = (Integer) ((Map) details.getDecodedDetails()).get("userId");

            if (userId != null)
                return userId.longValue();

            return null;
        });

        return opt.isPresent() ? opt.get() : null;
    }

    public static String getCurrentUsername() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof FlyUserDetails)
            return ((FlyUserDetails) authentication.getPrincipal()).getUsername();

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        if (requestAttributes != null) {
            String username = (String) requestAttributes.getAttribute(
                    "username",
                    RequestAttributes.SCOPE_REQUEST
            );

            if (username != null) {
                return username;
            }
        }

        return "";

    }
}
