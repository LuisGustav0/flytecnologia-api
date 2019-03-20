package com.flytecnologia.core.base.repository;

import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;
import com.flytecnologia.core.search.FlyPageableResult;
import org.hibernate.Session;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@NoRepositoryBean
public interface FlyRepository<T extends FlyEntity, PK extends Serializable, F extends FlyFilter>
        extends CrudRepository<T, PK>, FlyRepositorySearch<F> {
    EntityManager getEntityManager();
    EntityManagerFactory getEntityManagerFactory();

    Class<T> getEntityClass();

    String getEntityName();

    Optional<T> getReference(Long id);

    Optional<T> getReference(Long id, String tenant);

    Optional<T> find(Long id);

    Optional<T> find(Long id, String tenant);

    <N> Optional<N> getFieldById(Long id, String property);

    <N> Optional<N> getFieldById(Long id, String property, String tenant);

    void flush();

    Map<String, String> findImageById(Long id, String field);

    <G extends FlyEntity> void detach(G entity);

    void batchSave(List<T> entities, int batchSize);

    boolean isInactive(Long id);

    void delete(T entity, String tenant);

    Session getNewSession(String tenant);

    void rollbackSessionTransaction(Session session);

    void closeSession(Session session);

    <N> Optional<N> getValue(StringBuilder hql, Long id);

    <N> Optional<N> getValue(String hql, Long id);

    <N> Optional<N> getValue(StringBuilder hql, Long id, String tenant);

    <N> Optional<N> getValue(String hql, Long id, String tenant);

    <N> Optional<N> getValue(String hql, Map<String, Object> parameters);

    <N> Optional<N> getValue(StringBuilder hql, Map<String, Object> parameters);

    <N> Optional<N> getValue(StringBuilder hql, Map<String, Object> parameters, String tenant);

    <N> Optional<N> getValue(String hql, Map<String, Object> parameters, String tenant);

    Optional<List<T>> findAll(String tenant);

    Optional<List<T>> findAll(String columnReference, Object value);

    <N> Optional<List<N>> findAll(String columnReference, Object value, Class<?> nClass);

    Optional<List<T>> findAll(String columnReference, Object value, String tenant);

    <N> Optional<List<N>> findAll(String columnReference, Object value, Class<?> nClass, String tenant);

    Optional<T> findByInstruction(String hql);

    Optional<T> findByInstruction(StringBuilder hql);

    Optional<T> findByInstruction(String hql, String tenant);

    Optional<T> findByInstruction(StringBuilder hql, String tenant);

    Optional<T> findByInstruction(StringBuilder hql, Map<String, ?> parameters, String tenant);

    <N> Optional<N> findByInstruction(StringBuilder hql, Map<String, ?> parameters, Class<?> nClass, String tenant);

    Optional<T> findByInstruction(String hql, Map<String, ?> parameters, String tenant);

    <N> Optional<N> findByInstruction(String hql, Map<String, ?> parameters, Class<?> nClass, String tenant);

    Optional<List<T>> findAllByInstruction(StringBuilder hql, Map<String, ?> parameters);

    Optional<List<T>> findAllByInstruction(StringBuilder hql, Map<String, ?> parameters, String tenant);

    Optional<List<T>> findAllByInstruction(String hql, Map<String, ?> parameters);

    Optional<List<T>> findAllByInstruction(String hql, Map<String, ?> parameters, String tenant);

    <N> Optional<List<N>> findAllByInstruction(StringBuilder hql, Map<String, ?> parameters, Class<?> nClass, String tenant);

    <N> Optional<List<N>> findAllByInstruction(String hql, Map<String, ?> parameters, Class<?> nClass, String tenant);

    FlyPageableResult search(F filter, Pageable pageable);

    Optional<Long> getFirstId(F filter);

    Optional<Long> getPreviousId(F filter);

    Optional<Long> getLastId(F filter);

    Optional<Long> getNextId(F filter);

    Optional<List<Map<String, Object>>> getItemsAutocomplete(F filter);

    Optional<Map> getItemAutocomplete(F filter);

    Optional<Long> getRecordListCount(Long id, String listName);

    void setTenantInCurrentConnection(String tenantIdentifier);

    boolean existsById(Long id, String tenant);
}