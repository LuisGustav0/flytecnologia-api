package com.flytecnologia.core.base.repository.plus;

import com.flytecnologia.core.base.service.plus.FlyValidationService;
import com.flytecnologia.core.model.FlyEntity;
import org.hibernate.Session;

public interface FlyDeleteByTenantRepository<T extends FlyEntity> extends
        FlyValidationService,
        FlyHibernateSessionRepository {

    default void delete(T entity, String tenant) {
        if (isEmpty(tenant) || isEmpty(entity))
            return;

        final Session session = getNewSession(tenant);

        try {
            session.delete(entity);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            throw new RuntimeException(e.getMessage());
        }
    }
}
