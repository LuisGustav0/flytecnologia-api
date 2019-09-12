package com.flytecnologia.core.base.repository.plus;

import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;

import java.util.Optional;

public interface FlyInactiveRepository<T extends FlyEntity, F extends FlyFilter> extends
        FlyFindValueRepository<T> {

    default void addInactiveFilter(F filter, StringBuilder hqlWhere, String entityName) {
        if (!filter.isIgnoreInactiveFilter() && filter.getInactive() != null) {
            hqlWhere.append("   and ")
                    .append(entityName)
                    .append(".inactive is ")
                    .append(filter.getInactive())
                    .append("\n");
        }
    }

    default boolean isInactive(Long id) {
        if (id == null) {
            return false;
        }

        final Optional<Boolean> inative = getFieldById(id, "inactive");

        return inative.orElse(false);
    }
}
