package com.flytecnologia.core.hibernate.multitenancy;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("basic-security")
@Component
public class FlyTenantBasicIdentifierResolver implements CurrentTenantIdentifierResolver {

    @Override
    public String resolveCurrentTenantIdentifier() {
        return "client_0001";
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
