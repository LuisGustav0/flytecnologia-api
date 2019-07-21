package com.flytecnologia.core.base.controller.plus;

import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

public interface FlyDeleteController<T extends FlyEntity, F extends FlyFilter> extends
        FlyGetServiceController<T, F> {
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority(getAuthorityDelete()) and #oauth2.hasScope('write')")
    default void delete(@PathVariable Long id) {
        getService().delete(id);
    }
}
