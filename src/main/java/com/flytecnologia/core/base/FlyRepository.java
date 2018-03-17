package com.flytecnologia.core.base;

import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@NoRepositoryBean
public interface FlyRepository<T extends FlyEntity, PK extends Serializable, F extends FlyFilter>
        extends JpaRepository<T, PK>, CrudRepository<T, PK>, FlyRepositorySearch<F> {
    EntityManager getEntityManager();

    Class<T> getEntityClass();

    String getEntityName();

    T getReference(Long id);

    Optional<List> getItensAutocomplete(F filter);

    Optional<Map> getItemAutocomplete(F filter);

    boolean isEmpty(Object value);
}
