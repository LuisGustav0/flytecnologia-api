package com.flytecnologia.core.base.service.plus;

import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;

import java.util.Optional;

public interface FlyEntityReferenceService<T extends FlyEntity, F extends FlyFilter>
        extends FlyGetRepositoryService<T, F> {
    default Optional<T> getReference(Long id) {
        return getRepository().getReference(id);
    }

    default Optional<T> getReference(Long id, String tenant) {
        return getRepository().getReference(id, tenant);
    }
}
