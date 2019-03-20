package com.flytecnologia.core.base.repository.plus;

import com.flytecnologia.core.model.FlyEntity;
import lombok.NonNull;

import java.util.Optional;

public interface FlyExistsRepository<T extends FlyEntity> extends
        FlyFindValueRepository<T> {

    default boolean existsById(@NonNull Long id, @NonNull String tenant) {
        final String entityName = getEntityName();

        final String ql =
                "SELECT CASE WHEN COUNT(b) > 0 THEN true " +
                        "ELSE false END " +
                        "FROM " + entityName + " b WHERE b.id = :id";

        final Optional<Boolean> value = getValue(ql, id, tenant);

        return value.orElse(false);
    }
}
