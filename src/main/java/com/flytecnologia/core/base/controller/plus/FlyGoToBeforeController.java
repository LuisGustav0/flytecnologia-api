package com.flytecnologia.core.base.controller.plus;

import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;

public interface FlyGoToBeforeController<T extends FlyEntity, F extends FlyFilter> extends
        FlyGetServiceController<T, F> {
    @GetMapping("/before")
    @PreAuthorize("hasAuthority(getAuthorityRead()) and #oauth2.hasScope('read')")
    default ResponseEntity<Long> goToBefore(F filter) {
        return getService().goToBefore(filter)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
