package com.flytecnologia.core.base;

import com.flytecnologia.core.search.FlyFilter;
import com.flytecnologia.core.search.FlyPageableResult;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FlyRepositorySearch<F extends FlyFilter> {

    FlyPageableResult search(F filter, Pageable pageable);

    Optional<Long> getFirstId(F filter);

    Optional<Long> getPreviousId(F filter);

    Optional<Long> getLastId(F filter);

    Optional<Long> getNextId(F filter);

    Optional<List<Map<String, Object>>> getItemsAutocomplete(F filter);

    Optional<Map> getItemAutocomplete(F filter);
}
