package com.flytecnologia.core.base;

import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyAutoCompleteFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@NoRepositoryBean
public interface FlyRepository<T extends FlyEntity, PK extends Serializable> extends JpaRepository<T, PK> {
    EntityManager getEntityManager();

    Class<T> getEntityClass();

    String getEntityName();

    T getReference(Long id);

    List<Map<String, Object>> getItensAutocomplete(FlyAutoCompleteFilter acFilter, Map<String, Object> params);
    Map<String, Object> getItemAutocomplete(FlyAutoCompleteFilter acFilter, Map<String, Object> params);

    boolean isEmpty(Object value);
}
