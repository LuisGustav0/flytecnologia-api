package com.flytecnologia.core.base.controller.plus;

import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.net.URI;

public interface FlyCreateController<T extends FlyEntity, F extends FlyFilter> extends
        FlyGetServiceController<T, F> {
    default void addHeaderLocationForCreation(HttpServletResponse response, Long id) {
        final URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}")
                .buildAndExpand(id).toUri();
        response.setHeader("Location", uri.toASCIIString());
    }

    @PostMapping
    @PreAuthorize("hasAuthority(getAuthorityCreate()) and #oauth2.hasScope('write')")
    default ResponseEntity<T> create(@RequestBody T entity,
                                     HttpServletResponse response) {
        entity = getService().create(entity);

        addHeaderLocationForCreation(response, entity.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(entity);
    }
}
