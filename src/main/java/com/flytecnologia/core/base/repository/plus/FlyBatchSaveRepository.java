package com.flytecnologia.core.base.repository.plus;

import com.flytecnologia.core.model.FlyEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;
import java.util.Map;

public interface FlyBatchSaveRepository<T extends FlyEntity> extends FlyEntityManagerRepository {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    default void batchSave(List<T> entities, int batchSize) {
        final int entityCount = entities.size();

        EntityManager entityManager = null;
        EntityTransaction transaction = null;

        try {
            entityManager = getEntityManagerFactory().createEntityManager();

            transaction = entityManager.getTransaction();
            transaction.begin();

            for (int i = 0; i < entityCount; ++i) {
                if (i > 0 && i % batchSize == 0) {
                    entityManager.flush();
                    entityManager.clear();
                }

                T entity = entities.get(i);

                Map<String, Object> parameters = entity.getParameters();

                entity = entityManager.merge(entities.get(i));

                entity.setParameters(parameters);
            }

            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

}
