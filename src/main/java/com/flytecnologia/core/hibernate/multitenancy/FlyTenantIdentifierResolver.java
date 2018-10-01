package com.flytecnologia.core.hibernate.multitenancy;

import com.flytecnologia.core.token.FlyTokenUserDetails;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("oauth-security")
@Component
public class FlyTenantIdentifierResolver implements CurrentTenantIdentifierResolver {

    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenantId = FlyTokenUserDetails.getCurrentSchemaName();

        if (tenantId != null) {
            return FlyMultiTenantConstants.DEFAULT_TENANT_SUFFIX + tenantId;
        }

        String tenant = FlyTenantThreadLocal.getTenant();

        if (tenant != null)
            return tenant;

        return FlyMultiTenantConstants.DEFAULT_TENANT_ID;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
