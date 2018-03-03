package com.flytecnologia.core.base;

import com.flytecnologia.core.search.FlyFilter;
import com.flytecnologia.core.search.FlyPageableResult;
import org.springframework.data.domain.Pageable;

public interface FlyRepositorySearch<F extends FlyFilter> {

    FlyPageableResult search(F filter, Pageable pageable);

    Long getFirstId(F filter);

    Long getPreviousId(F filter);

    Long getLastId(F filter);

    Long getNextId(F filter);
}
