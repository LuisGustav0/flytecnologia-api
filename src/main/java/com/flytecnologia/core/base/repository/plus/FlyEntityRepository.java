package com.flytecnologia.core.base.repository.plus;

import com.flytecnologia.core.model.FlyEntity;

public interface FlyEntityRepository<T extends FlyEntity> {
    default String getEntityName() {
        return getEntityClass().getSimpleName();
    }

    Class<T> getEntityClass();
}
