package com.flytecnologia.core.base.repository;

import com.flytecnologia.core.base.repository.plus.*;
import com.flytecnologia.core.base.service.plus.FlyEntityClassService;
import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

@NoRepositoryBean
public abstract class FlyRepositoryImpl<T extends FlyEntity, F extends FlyFilter> implements
        FlyHibernateSessionRepository,
        FlyHibernateExecuteNativeQueryWithoutTransactionRepository,
        FlyEntityReferenceRepository<T>,
        FlyFindByInstructionRepository<T>,
        FlyAutocompleteRepository<T, F>,
        FlySearchRepository<T, F>,
        FlyFindNextRepository<T, F>,
        FlyEntityManagerRepository,
        FlyTenantRepository,
        FlyFindValueRepository<T>,
        FlyFindAllRepository<T>,
        FlyFindRepository<T>,
        FlyResultListRepository<T>,
        FlyBatchSaveRepository<T>,
        FlyDeleteByTenantRepository<T>,
        FlyInactiveRepository<T, F>,
        FlyRecordCountRepository<T>,
        FlyExistsRepository<T>,
        FlyEntityClassService<T> {

    private EntityManager entityManager;
    private EntityManagerFactory entityManagerFactory;

    public FlyRepositoryImpl(EntityManager entityManager,
                             EntityManagerFactory entityManagerFactory) {
        this.entityManager = entityManager;
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    @Override
    public EntityManagerFactory getEntityManagerFactory() {
        return this.entityManagerFactory;
    }
}