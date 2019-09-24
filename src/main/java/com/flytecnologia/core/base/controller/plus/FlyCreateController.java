package com.flytecnologia.core.base.controller.plus;

import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

public interface FlyCreateController<T extends FlyEntity, F extends FlyFilter> extends
        FlyGetServiceController<T, F> {

    @PostMapping
    @PreAuthorize("hasAuthority(getAuthorityCreate()) and #oauth2.hasScope('write')")
    default ResponseEntity<Void> create(@RequestBody T entity) {
        entity = getService().create(entity);

        return ResponseEntity.created(getLocation(entity.getId())).build();
    }

    default URI getLocation(Long id) {
        return ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}")
                .buildAndExpand(id).toUri();
    }
}
