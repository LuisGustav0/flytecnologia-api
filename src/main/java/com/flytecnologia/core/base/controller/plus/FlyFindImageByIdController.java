package com.flytecnologia.core.base.controller.plus;

import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

public interface FlyFindImageByIdController<T extends FlyEntity, F extends FlyFilter> extends
        FlyGetServiceController<T, F> {
    @GetMapping("/find-image-by-id/{id}/{field}")
    @PreAuthorize("#oauth2.hasScope('read')")
    default ResponseEntity<Map<String, String>> findImageById(@PathVariable Long id, @PathVariable String field) {
        return ResponseEntity.ok(getService().findImageById(id, field));
    }

}
