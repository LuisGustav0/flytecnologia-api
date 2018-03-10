package com.flytecnologia.core.token;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Map;
import java.util.Optional;

public class FlyTokenUserDetails {
    private static Optional<Authentication> getAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
    }

    public static Optional<Map<String, Object>> getAuthenticationDecodedDetails() {
        Optional<Authentication> authentication = getAuthentication();

        return authentication.map(a -> {
            if (!(a.getDetails() instanceof OAuth2AuthenticationDetails))
                return null;

            OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) a.getDetails();
            return (Map<String, Object>) details.getDecodedDetails();
        });
    }

    public static Optional<Object> getAuthenticationInformation(String key) {
        Optional<Map<String, Object>> auten = getAuthenticationDecodedDetails();

        if(auten.isPresent())
            return Optional.ofNullable(auten.get().get(key));


        return Optional.empty();
    }

    public static Long getCurrentUserId() {
        Optional<Object> userId = getAuthenticationInformation("userId");

        return userId.map(o -> ((Integer) o).longValue()).orElse(null);

    }

    public static String getCurrentUsername() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();


        if (authentication != null) {
            if (authentication.getPrincipal() instanceof String)
                return (String) authentication.getPrincipal();

            if (authentication.getPrincipal() instanceof UserDetails)
                return ((UserDetails) authentication.getPrincipal()).getUsername();
        }

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        if (requestAttributes != null) {
            return (String) requestAttributes.getAttribute(
                    "username",
                    RequestAttributes.SCOPE_REQUEST
            );
        }

        return "";

    }
}
