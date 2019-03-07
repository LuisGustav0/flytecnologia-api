package com.flytecnologia.core.base.repository.plus;

import com.flytecnologia.core.model.FlyEntity;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public interface FlyEntityManagerRepository {
    EntityManager getEntityManager();

    EntityManagerFactory getEntityManagerFactory();

    default <G extends FlyEntity> void detach(G entity) {
        if (entity != null)
            getEntityManager().detach(entity);
    }

    default void flush() {
        getEntityManager().flush();
    }

}
