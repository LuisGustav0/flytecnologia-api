package com.flytecnologia.core.base.plusService;

import com.flytecnologia.core.base.FlyValidationBase;
import com.flytecnologia.core.hibernate.multitenancy.FlyMultiTenantConstants;
import com.flytecnologia.core.hibernate.multitenancy.FlyTenantThreadLocal;
import com.flytecnologia.core.token.FlyTokenUserDetails;

import java.util.Collection;
import java.util.function.Consumer;

public interface FlyServiceParallelForEach extends FlyValidationBase {
    default <E> void parallelForEach(Collection<E> collection, Consumer<E> consumer) {

        String tenantId = FlyTenantThreadLocal.getTenant();
        Long userId = FlyTenantThreadLocal.getUserId();

        if (isEmpty(tenantId)) {
            tenantId = FlyMultiTenantConstants.DEFAULT_TENANT_SUFFIX + FlyTokenUserDetails.getCurrentSchemaName();
        }

        if (isEmpty(userId))
            userId = FlyTokenUserDetails.getCurrentUserId();

        String finalTenantId = tenantId;
        Long finalUserId = userId;

        collection.parallelStream().forEach(o -> {

            FlyTenantThreadLocal.setTenant(finalTenantId);
            FlyTenantThreadLocal.setUserId(finalUserId);

            consumer.accept(o);
        });
    }
}
