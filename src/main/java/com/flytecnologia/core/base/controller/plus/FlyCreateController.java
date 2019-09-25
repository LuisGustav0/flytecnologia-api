package com.flytecnologia.core.base.controller.plus;

import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public interface FlyCreateController<T extends FlyEntity, F extends FlyFilter> extends
        FlyGetServiceController<T, F> {

    @PostMapping
    @PreAuthorize("hasAuthority(getAuthorityCreate()) and #oauth2.hasScope('write')")
    default ResponseEntity<Object> create(@RequestBody T entity) {
        entity = getService().create(entity);

        return ResponseEntity
                .created(getLocation(entity.getId()))
                .body(getResponseBodyWhenCreate(entity));
    }

    default URI getLocation(@NonNull Long id) {
        return ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}")
                .buildAndExpand(id).toUri();
    }

    default Object getResponseBodyWhenCreate(@NonNull T entity) {
        Map<String, Long> response = new HashMap<>();
        response.put("id", entity.getId());

        return response;
    }
}
