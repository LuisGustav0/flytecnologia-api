package com.flytecnologia.core.base;

import com.flytecnologia.core.search.FlyFilter;
import com.flytecnologia.core.search.FlyPageableResult;
import org.springframework.data.domain.Pageable;

public interface FlyRepositorySearch<F extends FlyFilter> {

    FlyPageableResult search(F filter, Pageable pageable);

}
