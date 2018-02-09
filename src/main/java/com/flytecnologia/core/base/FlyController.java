package com.flytecnologia.core.base;

import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyAutoCompleteFilter;
import com.flytecnologia.core.spring.ValidatorUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public abstract class FlyController<T extends FlyEntity> {
    protected abstract FlyService<T> getService();

    protected void addHeaderLocationForCreation(HttpServletResponse response, Long id) {
        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}")
                .buildAndExpand(id).toUri();
        response.setHeader("Location", uri.toASCIIString());
    }

    @PostMapping
    @PreAuthorize("hasAuthority(getAuthorityCreate()) and #oauth2.hasScope('write')")
    public ResponseEntity<T> save(@Valid @RequestBody EntityAux<T> entityAux,
                                  HttpServletResponse response)
            throws MethodArgumentNotValidException, SecurityException {

        ValidatorUtil.validate(entityAux.getEntity(), this.getClass(), "save");

        entityAux.getEntity().setParameters(entityAux.getParameters());

        T entity = getService().save(entityAux.getEntity());

        addHeaderLocationForCreation(response, entity.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(entity);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority(getAuthorityRead()) and #oauth2.hasScope('read')")
    public ResponseEntity<T> findById(@PathVariable Long id) {
        T entity = getService().findById(id);
        return entity != null ? ResponseEntity.ok(entity) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority(getAuthorityDelete()) and #oauth2.hasScope('write')")
    public void remover(@PathVariable Long id) {
        getService().delete(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority(getAuthorityUpdate()) and #oauth2.hasScope('write')")
    public ResponseEntity<T> update(@PathVariable Long id, @Valid @RequestBody EntityAux<T> entityAux)
            throws MethodArgumentNotValidException {

        ValidatorUtil.validate(entityAux.getEntity(), this.getClass(), "update");

        entityAux.getEntity().setParameters(entityAux.getParameters());

        T entity = getService().update(id, entityAux.getEntity());

        return ResponseEntity.ok(entity);
    }

    @GetMapping(value = "/today")
    public LocalDate getToday() {
        return LocalDate.now();
    }

    /*@GetMapping
    @PreAuthorize("hasAuthority(getAuthorityRead()) and #oauth2.hasScope('read')")
    public FlyPageableResult search(FlyFilter filter, Pageable pageable) {
        return getService().search(filter, pageable);
    }*/

    @GetMapping(value = "/defaultValuesCrud")
    public Map<String, Object> defaultValuesCrud() {
        return getService().defaultValuesCrud();
    }

    @GetMapping(value = "/defaultValuesSearch")
    public Map<String, Object> defaultValuesSearch() {
        return getService().defaultValuesSearch();
    }

    @GetMapping(value = "/autocomplete")
    @PreAuthorize("#oauth2.hasScope('read')")
    public ResponseEntity<List<Map<String, Object>>> autocomplete(FlyAutoCompleteFilter acFilter, Map<String, Object> params) {
        return new ResponseEntity<>(getService().getListAutocomplete(acFilter, params), HttpStatus.OK);
    }

    static class EntityAux<T extends FlyEntity> {
        private Map<String, Object> parameters;
        private T entity;

        public EntityAux() {
        }

        public Map<String, Object> getParameters() {
            return parameters;
        }

        public void setParameters(Map<String, Object> parameters) {
            this.parameters = parameters;
        }

        public T getEntity() {
            return entity;
        }

        public void setEntity(T entity) {
            this.entity = entity;
        }
    }
}
