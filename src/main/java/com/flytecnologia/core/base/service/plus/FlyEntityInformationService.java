package com.flytecnologia.core.base.service.plus;

import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;

public interface FlyEntityInformationService<T extends FlyEntity, F extends FlyFilter> extends
        FlyGetRepositoryService<T, F> {
    default Class<T> getEntityClass() {
        return getRepository().getEntityClass();
    }

    default String getEntityName() {
        return getRepository().getEntityName();
    }
}
