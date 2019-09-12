package com.flytecnologia.core.token;

import com.flytecnologia.core.exception.InvalidSessionException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Map;
import java.util.Optional;

import static com.flytecnologia.core.hibernate.multitenancy.FlyMultiTenantConstants.DEFAULT_TENANT_ID;
import static com.flytecnologia.core.hibernate.multitenancy.FlyMultiTenantConstants.REQUEST_HEADER_ID;

public class FlyTokenUserDetails {
    private FlyTokenUserDetails() {}

    private static Optional<Authentication> getAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
    }

    public static Optional<Map> getAuthenticationDecodedDetails() {
        final Optional<Authentication> authentication = getAuthentication();

        return authentication.map(a -> {
            if (!(a.getDetails() instanceof OAuth2AuthenticationDetails))
                return null;

            OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) a.getDetails();
            return (Map<String, Object>) details.getDecodedDetails();
        });
    }

    public static Optional<Object> getAuthenticationInformation(String key) {
        final Optional<Map> auten = FlyTokenUserDetails.getAuthenticationDecodedDetails();

        return auten.map(map -> map.get(key));
    }

    public static Long getCurrentUserId() {
        final Optional<Object> userId = getAuthenticationInformation("userId");

        return userId.map(o -> ((Integer) o).longValue()).orElse(null);
    }

    public static String getCurrentSchemaName() {
        final Optional<Object> tenant = getAuthenticationInformation(
                REQUEST_HEADER_ID);

        return (String) tenant.orElse(DEFAULT_TENANT_ID);
    }

    public static String getCurrentSchemaNameOrElseNull() {
        final Optional<Object> tenant = getAuthenticationInformation(REQUEST_HEADER_ID);

        return (String) tenant.orElse(null);
    }

    public static String getCurrentUsername() {
        final SecurityContext securityContext = SecurityContextHolder.getContext();
        final Authentication authentication = securityContext.getAuthentication();


        if (authentication != null) {
            if (authentication.getPrincipal() instanceof String)
                return (String) authentication.getPrincipal();

            if (authentication.getPrincipal() instanceof UserDetails)
                return ((UserDetails) authentication.getPrincipal()).getUsername();
        }

        final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        if(requestAttributes == null)
            throw new InvalidSessionException();

        return (String) requestAttributes.getAttribute(
                "username",
                RequestAttributes.SCOPE_REQUEST
        );

    }
}
