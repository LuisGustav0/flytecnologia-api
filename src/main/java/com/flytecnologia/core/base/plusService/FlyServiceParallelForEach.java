package com.flytecnologia.core.base.plusService;

import com.flytecnologia.core.hibernate.multitenancy.FlyTenantThreadLocal;

import java.util.Collection;
import java.util.function.Consumer;

public interface FlyServiceParallelForEach extends FlyValidationBase, FlyTenantInformation {
    default <E> void parallelForEach(Collection<E> collection, Consumer<E> consumer) {
        final String finalTenantId = getTenant();
        final Long finalUserId = getUserId();

        collection.parallelStream().forEach(o -> {

            FlyTenantThreadLocal.setTenant(finalTenantId);
            FlyTenantThreadLocal.setUserId(finalUserId);

            consumer.accept(o);
        });
    }
}
