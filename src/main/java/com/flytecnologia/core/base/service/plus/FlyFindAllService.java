package com.flytecnologia.core.base.service.plus;

import com.flytecnologia.core.base.repository.FlyRepository;
import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;

import java.util.List;
import java.util.Optional;

public interface FlyFindAllService<T extends FlyEntity, F extends FlyFilter> {
    FlyRepository<T, Long, F> getRepository();

    default Optional<List<T>> findAll(String tenant) {
        return getRepository().findAll(tenant);
    }

    default Optional<List<T>> findAll(String columnReference, Object value, String tenant) {
        return getRepository().findAll(columnReference, value, tenant);
    }

    default Optional<List<T>> findAll(String columnReference, Object value) {
        return getRepository().findAll(columnReference, value);
    }

    default <N> Optional<List<N>> findAll(String columnReference,
                                          Object value, Class<?> nClass, String tenant) {
        return getRepository().findAll(columnReference, value, nClass, tenant);
    }

    default <N> Optional<List<N>> findAll(String columnReference,
                                          Object value, Class<?> nClass) {
        return getRepository().findAll(columnReference, value, nClass);
    }
}
