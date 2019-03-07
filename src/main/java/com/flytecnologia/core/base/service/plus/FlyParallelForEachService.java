package com.flytecnologia.core.base.service.plus;

import com.flytecnologia.core.hibernate.multitenancy.FlyTenantThreadLocal;

import java.util.Collection;
import java.util.function.Consumer;

public interface FlyParallelForEachService extends FlyValidationService, FlyTenantInformationService {
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
