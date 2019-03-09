package com.flytecnologia.core.base.controller.plus;

import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

public interface FlyAutocompleteController<T extends FlyEntity, F extends FlyFilter> extends
        FlyGetServiceController<T, F> {
    @GetMapping(value = "/autocomplete/list")
    @PreAuthorize("#oauth2.hasScope('read')")
    default ResponseEntity<List> getItemsAutocomplete(F filter) {
        return new ResponseEntity<>(getService().getItemsAutocomplete(filter).orElse(null), HttpStatus.OK);
    }

    @GetMapping(value = "/autocomplete/item")
    @PreAuthorize("#oauth2.hasScope('read')")
    default ResponseEntity<Map> getItemAutocomplete(F filter) {
        return new ResponseEntity<>(getService().getItemAutocomplete(filter).orElse(null), HttpStatus.OK);
    }

}
