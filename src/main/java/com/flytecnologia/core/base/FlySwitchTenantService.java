package com.flytecnologia.core.base;

import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

@Service
public class FlySwitchTenantService {
    private EntityManagerFactory entityManagerFactory;

    public FlySwitchTenantService(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public void bindSession() {
        if (!TransactionSynchronizationManager.hasResource(entityManagerFactory)) {
            final EntityManager entityManager = entityManagerFactory.createEntityManager();
            TransactionSynchronizationManager.bindResource(entityManagerFactory, new EntityManagerHolder(entityManager));
        }
    }

    public void unbindSession() {
        final EntityManagerHolder emHolder = (EntityManagerHolder) TransactionSynchronizationManager
                .unbindResource(entityManagerFactory);
        EntityManagerFactoryUtils.closeEntityManager(emHolder.getEntityManager());
    }
}
