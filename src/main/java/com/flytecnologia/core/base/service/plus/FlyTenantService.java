package com.flytecnologia.core.base.service.plus;

import com.flytecnologia.core.hibernate.multitenancy.FlyTenantThreadLocal;
import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;

import static com.flytecnologia.core.base.service.plus.FlyValidateEmptyService.isEmpty;

public interface FlyTenantService<T extends FlyEntity, F extends FlyFilter> extends
        FlyGetRepositoryService<T, F> {
    default void setTenantInCurrentSession(String tenant) {
        if (isEmpty(tenant))
            return;

        FlyTenantThreadLocal.setTenant(tenant);

        getRepository().setTenantInCurrentConnection(tenant);
    }
}