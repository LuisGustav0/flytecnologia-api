package com.flytecnologia.core.base.service.plus;

import com.flytecnologia.core.hibernate.multitenancy.FlyTenantThreadLocal;
import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;

import static com.flytecnologia.core.base.service.plus.FlyValidateEmptyService.isEmpty;

public interface FlyTenantService<T extends FlyEntity, F extends FlyFilter> extends
        FlyGetRepositoryService<T, F> {
    default void setTenantInCurrentSession(String tenant) {
        setTenantInCurrentSession(tenant, null);
    }

    default void setTenantInCurrentSession(String tenant, Long userId) {
        if (isEmpty(tenant))
            return;

        FlyTenantThreadLocal.setTenant(tenant);

        if(!isEmpty(userId)) {
            FlyTenantThreadLocal.setUserId(userId);
        }

        getRepository().setTenantInCurrentConnection(tenant);
    }
}