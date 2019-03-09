package com.flytecnologia.core.base.service.plus;

import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;

import java.util.Optional;

public interface FlyGoToService<T extends FlyEntity, F extends FlyFilter> extends
        FlyFindNextService<T, F> {
    default Optional<Long> goToBefore(F filter) {
        if (filter.getId() == null || filter.getId() == 0) {
            return getFirstId(filter);
        }

        return getPreviousId(filter);
    }

    default Optional<Long> goToAfter(F filter) {
        if (filter.getId() == null || filter.getId() == 0) {
            return getLastId(filter);
        }

        return getNextId(filter);
    }
}
