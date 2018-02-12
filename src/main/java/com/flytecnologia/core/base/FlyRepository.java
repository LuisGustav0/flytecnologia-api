package com.flytecnologia.core.base;

import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@NoRepositoryBean
public interface FlyRepository<T extends FlyEntity, PK extends Serializable, F extends FlyFilter>
        extends JpaRepository<T, PK>, FlyRepositorySearch<F> {
    EntityManager getEntityManager();

    Class<T> getEntityClass();

    String getEntityName();

    T getReference(Long id);

    List<Map<String, Object>> getItensAutocomplete(F filter);

    Map<String, Object> getItemAutocomplete(F filter);

    boolean isEmpty(Object value);
}
