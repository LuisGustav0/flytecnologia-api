package com.flytecnologia.core.base.repository.plus;

import com.flytecnologia.core.hibernate.multitenancy.FlyMultiTenantConstants;
import org.springframework.transaction.annotation.Transactional;

public interface FlyTenantRepository extends FlyEntityManagerRepository {

    @Transactional
    default void setTenantInCurrentConnection(String tenantIdentifier) {
        flush();

        if (tenantIdentifier != null) {
            tenantIdentifier = "SET search_path TO  " + tenantIdentifier;
        } else {
            tenantIdentifier = "SET search_path TO  " + FlyMultiTenantConstants.DEFAULT_TENANT_ID;
        }

        getEntityManager().createNativeQuery(tenantIdentifier).executeUpdate();

        flush();
    }
}
