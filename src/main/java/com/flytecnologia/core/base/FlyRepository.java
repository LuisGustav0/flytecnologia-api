package com.flytecnologia.core.base;

import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.EntityManager;
import java.io.Serializable;

@NoRepositoryBean
public interface FlyRepository<T extends FlyEntity, PK extends Serializable, F extends FlyFilter>
        extends CrudRepository<T, PK>, FlyRepositorySearch<F> {
    EntityManager getEntityManager();

    Class<T> getEntityClass();

    String getEntityName();

    T getReference(Long id);

    boolean isEmpty(Object value);
}
