package com.flytecnologia.core.base.controller.plus;

import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;

public interface FlyGoToAfterController<T extends FlyEntity, F extends FlyFilter> extends
        FlyGetServiceController<T, F> {
    @GetMapping("/after")
    @PreAuthorize("hasAuthority(getAuthorityRead()) and #oauth2.hasScope('read')")
    default ResponseEntity<Long> goToAfter(F filter) {
        return getService().goToAfter(filter)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
