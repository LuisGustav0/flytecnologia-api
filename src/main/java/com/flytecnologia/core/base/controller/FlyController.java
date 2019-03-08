package com.flytecnologia.core.base.controller;

import com.flytecnologia.core.base.service.FlyService;
import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;
import com.flytecnologia.core.search.FlyPageableResult;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public abstract class FlyController<T extends FlyEntity, F extends FlyFilter> {
    protected abstract FlyService<T, F> getService();

    protected void addHeaderLocationForCreation(HttpServletResponse response, Long id) {
        final URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}")
                .buildAndExpand(id).toUri();
        response.setHeader("Location", uri.toASCIIString());
    }

    @PostMapping
    @PreAuthorize("hasAuthority(getAuthorityCreate()) and #oauth2.hasScope('write')")
    public ResponseEntity<T> create(@RequestBody T entity,
                                    HttpServletResponse response) {
        entity = getService().create(entity);

        addHeaderLocationForCreation(response, entity.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(entity);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority(getAuthorityRead()) and #oauth2.hasScope('read')")
    public ResponseEntity<T> findById(@PathVariable Long id) {
        return getService().find(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/after")
    @PreAuthorize("hasAuthority(getAuthorityRead()) and #oauth2.hasScope('read')")
    public ResponseEntity<Long> goToAfter(F filter) {
        return getService().goToAfter(filter)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/before")
    @PreAuthorize("hasAuthority(getAuthorityRead()) and #oauth2.hasScope('read')")
    public ResponseEntity<Long> goToBefore(F filter) {
        return getService().goToBefore(filter)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority(getAuthorityDelete()) and #oauth2.hasScope('write')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        getService().delete(id);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/find-image-by-id/{id}/{field}")
    @PreAuthorize("#oauth2.hasScope('read')")
    public ResponseEntity<Map<String, String>> findImageById(@PathVariable Long id, @PathVariable String field) {
        return ResponseEntity.ok(getService().findImageById(id, field));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority(getAuthorityUpdate()) and #oauth2.hasScope('write')")
    public ResponseEntity<T> update(@PathVariable Long id,
                                    @RequestBody T entity) {
        return ResponseEntity.ok(getService().update(id, entity));
    }

    @GetMapping(value = "/today")
    public LocalDate getToday() {
        return LocalDate.now();
    }

    @GetMapping(value = "/autocomplete/list")
    @PreAuthorize("#oauth2.hasScope('read')")
    public ResponseEntity<List> getItemsAutocomplete(F filter) {
        return new ResponseEntity<>(getService().getItemsAutocomplete(filter).orElse(null), HttpStatus.OK);
    }

    @GetMapping(value = "/autocomplete/item")
    @PreAuthorize("#oauth2.hasScope('read')")
    public ResponseEntity<Map> getItemAutocomplete(F filter) {
        return new ResponseEntity<>(getService().getItemAutocomplete(filter).orElse(null), HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasAuthority(getAuthorityRead()) and #oauth2.hasScope('read')")
    public FlyPageableResult search(F filter, Pageable pageable) {
        return getService().search(filter, pageable);
    }
}