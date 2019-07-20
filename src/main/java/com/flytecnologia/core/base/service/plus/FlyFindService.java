package com.flytecnologia.core.base.service.plus;

import com.flytecnologia.core.exception.BE;
import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;
import org.hibernate.Hibernate;

import java.util.Optional;

public interface FlyFindService<T extends FlyEntity, F extends FlyFilter> extends
        FlyGetRepositoryService<T, F> {
    default Optional<T> find(Long id) {
        return getRepository().find(id).map(entity -> (T) Hibernate.unproxy(entity));
    }

    default T findOrElseThrow(Long id, String msg) {
        return getRepository().find(id).orElseThrow(() -> new BE(msg));
    }

    default Optional<T> find(Long id, String tenant) {
        return getRepository().find(id, tenant);
    }
}
