package com.flytecnologia.core.base.repository.plus;

import com.flytecnologia.core.base.service.plus.FlyValidationService;
import com.flytecnologia.core.model.FlyEntity;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import java.util.Optional;

public interface FlyHibernateReferenceRepository<T extends FlyEntity> extends FlyHibernateSessionRepository,
        FlyValidationService {
    EntityManager getEntityManager();

    Class<T> getEntityClass();

    default Optional<T> getReference(Long id) {
        return getReference(id, null);
    }

    default Optional<T> getReference(Long id, String tenant) {
        if (isEmpty(id))
            return Optional.empty();

        final Session session = getNewSession(tenant);

        try {

            T entity;

            if (session != null) {
                entity = session.getReference(getEntityClass(), id);
            } else {
                entity = getEntityManager().getReference(getEntityClass(), id);
            }

            closeSession(session);

            if (entity == null)
                return Optional.empty();

            return Optional.of(entity);
        } catch (Exception e) {
            e.printStackTrace();
            rollbackSessionTransaction(session);
            throw new RuntimeException(e.getMessage());
        }
    }
}
