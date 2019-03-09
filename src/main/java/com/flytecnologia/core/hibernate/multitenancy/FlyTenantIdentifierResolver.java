package com.flytecnologia.core.hibernate.multitenancy;

import com.flytecnologia.core.base.service.plus.FlyTenantInformationService;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("oauth-security")
@Component
public class FlyTenantIdentifierResolver implements CurrentTenantIdentifierResolver {

    @Override
    public String resolveCurrentTenantIdentifier() {
        return FlyTenantInformationService.getTenant();
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
