package com.flytecnologia.core.base.service.plus;

import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;

public interface FlyExistsService<T extends FlyEntity, F extends FlyFilter> extends
        FlyGetRepositoryService<T, F> {

    default boolean existsById(Long id) {
        return getRepository().existsById(id);
    }

    default boolean existsById(Long id, String tenant) {
        return getRepository().existsById(id, tenant);
    }
}
