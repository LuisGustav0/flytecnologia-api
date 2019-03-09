package com.flytecnologia.core.base.service.plus;

import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;
import com.flytecnologia.core.search.FlyPageableResult;
import org.springframework.data.domain.Pageable;

public interface FlySearchService<T extends FlyEntity, F extends FlyFilter>
        extends FlyGetRepositoryService<T, F> {
    default FlyPageableResult search(F filter, Pageable pageable) {
        return getRepository().search(filter, pageable);
    }
}