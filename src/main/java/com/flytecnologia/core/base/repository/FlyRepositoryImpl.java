package com.flytecnologia.core.base.repository;

import com.flytecnologia.core.base.repository.plus.FlyAutocompleteRepository;
import com.flytecnologia.core.base.repository.plus.FlyBatchSaveRepository;
import com.flytecnologia.core.base.repository.plus.FlyDeleteByTenantRepository;
import com.flytecnologia.core.base.repository.plus.FlyEntityManagerRepository;
import com.flytecnologia.core.base.repository.plus.FlyEntityReferenceRepository;
import com.flytecnologia.core.base.repository.plus.FlyExistsRepository;
import com.flytecnologia.core.base.repository.plus.FlyFindAllRepository;
import com.flytecnologia.core.base.repository.plus.FlyFindByInstructionRepository;
import com.flytecnologia.core.base.repository.plus.FlyFindNextRepository;
import com.flytecnologia.core.base.repository.plus.FlyFindRepository;
import com.flytecnologia.core.base.repository.plus.FlyFindValueRepository;
import com.flytecnologia.core.base.repository.plus.FlyHibernateSessionRepository;
import com.flytecnologia.core.base.repository.plus.FlyInactiveRepository;
import com.flytecnologia.core.base.repository.plus.FlyRecordCountRepository;
import com.flytecnologia.core.base.repository.plus.FlyResultListRepository;
import com.flytecnologia.core.base.repository.plus.FlySearchRepository;
import com.flytecnologia.core.base.repository.plus.FlyTenantRepository;
import com.flytecnologia.core.base.service.plus.FlyEntityClassService;
import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

@NoRepositoryBean
public abstract class FlyRepositoryImpl<T extends FlyEntity, F extends FlyFilter> implements
        FlyHibernateSessionRepository,
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
        FlyRecordCountRepository<T, F>,
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