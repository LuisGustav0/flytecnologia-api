package com.flytecnologia.core.base.service.plus;

import com.flytecnologia.core.base.repository.FlyRepository;
import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;

public interface FlyGetRepositoryService  <T extends FlyEntity, F extends FlyFilter> {
    FlyRepository<T, Long, F> getRepository();
}
