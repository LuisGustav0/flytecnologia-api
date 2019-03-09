package com.flytecnologia.core.base.controller.plus;

import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface FlyUpdateController<T extends FlyEntity, F extends FlyFilter> extends
        FlyGetServiceController<T, F> {
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority(getAuthorityUpdate()) and #oauth2.hasScope('write')")
    default ResponseEntity<T> update(@PathVariable Long id,
                                    @RequestBody T entity) {
        return ResponseEntity.ok(getService().update(id, entity));
    }
}
