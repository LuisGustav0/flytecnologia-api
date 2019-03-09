package com.flytecnologia.core.base.controller.plus;

import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;
import com.flytecnologia.core.search.FlyPageableResult;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;

public interface FlySearchController<T extends FlyEntity, F extends FlyFilter> extends
        FlyGetServiceController<T, F> {
    @GetMapping
    @PreAuthorize("hasAuthority(getAuthorityRead()) and #oauth2.hasScope('read')")
    default FlyPageableResult search(F filter, Pageable pageable) {
        return getService().search(filter, pageable);
    }
}
