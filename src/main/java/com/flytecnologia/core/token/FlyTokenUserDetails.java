package com.flytecnologia.core.token;

import com.flytecnologia.core.hibernate.multitenancy.FlyMultiTenantConstants;
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

    public static Optional<Map> getAuthenticationDecodedDetails() {
        Optional<Authentication> authentication = getAuthentication();

        return authentication.map(a -> {
            if (!(a.getDetails() instanceof OAuth2AuthenticationDetails))
                return null;

            OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) a.getDetails();
            return (Map<String, Object>) details.getDecodedDetails();
        });
    }

    public static Optional<Object> getAuthenticationInformation(String key) {
        Optional<Map> auten = getAuthenticationDecodedDetails();

        return auten.map(map -> map.get(key));
    }

    public static Long getCurrentUserId() {
        Optional<Object> userId = getAuthenticationInformation("userId");

        return userId.map(o -> ((Integer) o).longValue()).orElse(null);
    }

    public static String getCurrentSchemaName() {
        Optional<Object> tenant = getAuthenticationInformation(FlyMultiTenantConstants.REQUEST_HEADER_ID);

        return (String) tenant.orElse(FlyMultiTenantConstants.DEFAULT_TENANT_ID);
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

        return (String) requestAttributes.getAttribute(
                "username",
                RequestAttributes.SCOPE_REQUEST
        );

    }
}
