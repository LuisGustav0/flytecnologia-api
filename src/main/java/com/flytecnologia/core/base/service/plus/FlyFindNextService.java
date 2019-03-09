package com.flytecnologia.core.base.service.plus;

import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;

import java.util.Optional;

public interface FlyFindNextService<T extends FlyEntity, F extends FlyFilter> extends
        FlyGetRepositoryService<T, F> {
    default Optional<Long> getFirstId(F filter) {
        return getRepository().getFirstId(filter);
    }

    default Optional<Long> getPreviousId(F filter) {
        return getRepository().getPreviousId(filter);
    }

    default Optional<Long> getLastId(F filter) {
        return getRepository().getLastId(filter);
    }

    default Optional<Long> getNextId(F filter) {
        return getRepository().getNextId(filter);
    }
}
