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
        String tenant = FlyTokenUserDetails.getCurrentSchemaNameOrElseNull();

        if (tenant != null) {
            return FlyMultiTenantConstants.DEFAULT_TENANT_SUFFIX + tenant;
        }

        tenant = FlyTenantThreadLocal.getTenant();

        if (tenant != null)
            return tenant;

        return FlyMultiTenantConstants.DEFAULT_TENANT_ID;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
