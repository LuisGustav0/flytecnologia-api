package com.flytecnologia.core.base.repository.plus;

import org.springframework.transaction.annotation.Transactional;

import static com.flytecnologia.core.hibernate.multitenancy.FlyMultiTenantConnectionProviderImpl.SET_SCHEMA;
import static com.flytecnologia.core.hibernate.multitenancy.FlyMultiTenantConstants.DEFAULT_TENANT_ID;

public interface FlyTenantRepository extends FlyEntityManagerRepository {
    @Transactional
    default void setTenantInCurrentConnection(String tenantIdentifier) {
        flush();

        if (tenantIdentifier == null) {
            tenantIdentifier = DEFAULT_TENANT_ID;
        }

        String sql = SET_SCHEMA + tenantIdentifier;

        getEntityManager().createNativeQuery(sql).executeUpdate();

        flush();
    }
}
