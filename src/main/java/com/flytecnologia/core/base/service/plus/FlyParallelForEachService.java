package com.flytecnologia.core.base.service.plus;

import com.flytecnologia.core.hibernate.multitenancy.FlyTenantThreadLocal;

import java.util.Collection;
import java.util.function.Consumer;

import static com.flytecnologia.core.base.service.plus.FlyTenantInformationService.getTenant;
import static com.flytecnologia.core.base.service.plus.FlyTenantInformationService.getUserId;

public class FlyParallelForEachService  {
    public static <E> void parallelForEach(Collection<E> collection, Consumer<E> consumer) {
        final String finalTenantId = getTenant();
        final Long finalUserId = getUserId();

        collection.parallelStream().forEach(o -> {

            FlyTenantThreadLocal.setTenant(finalTenantId);
            FlyTenantThreadLocal.setUserId(finalUserId);

            consumer.accept(o);
        });
    }
}
