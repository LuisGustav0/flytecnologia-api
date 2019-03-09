package com.flytecnologia.core.base.service.plus;

import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;

import java.util.Optional;

public interface FlyRecordCountService<T extends FlyEntity, F extends FlyFilter> extends
        FlyGetRepositoryService<T, F> {
    default Optional<Long> getRecordListCount(Long id, String listName) {
        return getRepository().getRecordListCount(id, listName);
    }
}
