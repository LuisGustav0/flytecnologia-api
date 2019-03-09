package com.flytecnologia.core.base.service.plus;

import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FlyAutocompleteService<T extends FlyEntity, F extends FlyFilter> extends
        FlyGetRepositoryService<T, F> {

    default Optional<List<Map<String, Object>>> getItemsAutocomplete(F filter) {
        beforeSearchAutoComplete(filter);

        return getRepository().getItemsAutocomplete(filter);
    }

    default Optional<Map> getItemAutocomplete(F filter) {
        beforeSearchAutoComplete(filter);

        return getRepository().getItemAutocomplete(filter);
    }

    default void beforeSearchAutoComplete(F filter) {
    }
}
