package com.flytecnologia.core.base.service.plus;

import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;

import java.util.Map;
import java.util.Optional;

public interface FlyFindValueService<T extends FlyEntity, F extends FlyFilter> extends
        FlyGetRepositoryService<T, F> {

    default Map<String, String> findImageById(Long id, String field) {
        return getRepository().findImageById(id, field);
    }

    default <N> Optional<N> getFieldById(Long id, String property) {
        return getRepository().getFieldById(id, property);
    }

    default <N> Optional<N> getFieldById(Long id, String property, String tenant) {
        return getRepository().getFieldById(id, property, tenant);
    }
}
