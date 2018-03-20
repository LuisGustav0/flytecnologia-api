package com.flytecnologia.core.hibernate.multitenancy;

import com.flytecnologia.core.token.FlyTokenUserDetails;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Profile("oauth-security")
@Component
public class FlyTenantIdentifierResolver implements CurrentTenantIdentifierResolver {

    @Override
    public String resolveCurrentTenantIdentifier() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        if (requestAttributes != null) {
            /*String tenantId = (String) requestAttributes.getAttribute(
                    FlyMultiTenantConstants.REQUEST_HEADER_ID,
                    RequestAttributes.SCOPE_REQUEST
            );*/

            String tenantId = FlyTokenUserDetails.getCurrentSchemaName();

            if (tenantId != null) {
                return FlyMultiTenantConstants.DEFAULT_TENANT_SUFFIX + tenantId;
            }
        }
        return FlyMultiTenantConstants.DEFAULT_TENANT_ID;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
