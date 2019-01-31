package com.flytecnologia.core.base;

import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@NoRepositoryBean
public interface FlyRepository<T extends FlyEntity, PK extends Serializable, F extends FlyFilter>
        extends CrudRepository<T, PK>, FlyRepositorySearch<F> {
    EntityManager getEntityManager();

    Class<T> getEntityClass();

    String getEntityName();

    Optional<T> getReference(Long id);

    Optional<T> find(Long id);

    Optional<T> find(Long id, String tenant);

    Optional<List<T>> findAll(String tenant, String columnReference, Object value);

    <E> Optional<E> getFieldById(Long id, String property);

    void flush();

    Map<String, String> findImageById(Long id, String field);

    <G extends FlyEntity> void detach(G entity);

    void batchSave(List<T> entities, int batchSize);

    boolean isInactive(Long id);

    boolean hasAnyPermission(String... roles);
}
