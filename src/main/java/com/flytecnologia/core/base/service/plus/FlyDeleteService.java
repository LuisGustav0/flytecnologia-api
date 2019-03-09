package com.flytecnologia.core.base.service.plus;

import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.flytecnologia.core.base.service.plus.FlyValidateEmptyService.notNull;

public interface FlyDeleteService<T extends FlyEntity, F extends FlyFilter> extends
        FlyGetRepositoryService<T, F>,
        FlyEntityInformationService<T, F>,
        FlyFindService<T, F> {
    default void beforeDelete(final T entity) {
    }

    default void afterDelete(Long id, String tenant) {
    }

    default void beforeDeleteAll(List<T> entities) {
    }

    default void afterDeleteAll(List<T> entities) {
    }

    @Transactional
    default void delete(Long id) {
        delete(id, false, false, null);
    }


    @Transactional
    default void delete(Long id, String tenant) {
        delete(id, false, false, tenant);
    }

    @Transactional
    default void delete(Long id,
                        boolean isIgnoreBeforeDelete,
                        boolean isIgnoreAfterDelete,
                        String tenant) {
        notNull(id, "flyserivice.idNotNull");

        final Optional<T> entityOptional = find(id, tenant);

        T entity = entityOptional
                .orElseThrow(() -> new EmptyResultDataAccessException("delete " + getEntityName() + " -> " + id, 1));

        if (!isIgnoreBeforeDelete) {
            if (tenant != null) {
                entity.getParameters().put("$tenant", tenant);
            }

            beforeDelete(entity);
        }

        if (tenant != null) {
            getRepository().delete(entity, tenant);
        } else {
            getRepository().delete(entity);
        }

        if (!isIgnoreAfterDelete) {
            afterDelete(id, tenant);
        }
    }


    @Transactional
    default void deleteAll(List<T> entities) {
        deleteAll(entities, false, false);
    }

    @Transactional
    default void deleteAll(List<T> entities, boolean isIgnoreBeforeDelete, boolean isIgnoreAfterDelete) {
        notNull(entities, "flyserivice.listOfEntityNotNull");

        if (!isIgnoreBeforeDelete) {
            beforeDeleteAll(entities);
        }

        getRepository().deleteAll(entities);

        if (!isIgnoreAfterDelete) {
            afterDeleteAll(entities);
        }
    }

}
