package com.flytecnologia.core.base.repository.plus;

import com.flytecnologia.core.base.service.plus.FlyEntityClassService;
import com.flytecnologia.core.model.FlyEntity;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FlyResultListRepository<T extends FlyEntity> extends
        FlyEntityManagerRepository, FlyEntityClassService<T> {

    default <L> Optional<List<Map<String, L>>> getResultListMap(StringBuilder hql) {
        return getResultListMap(hql, null, 0);
    }

    default <L> Optional<List<Map<String, L>>> getResultListMap(StringBuilder hql, Map<String, Object> parameters) {
        return getResultListMap(hql, parameters, 0);
    }

    default <L> Optional<List<Map<String, L>>> getResultListMap(
            StringBuilder hql,
            Map<String, Object> parameters,
            int limit) {
        final Query query = getEntityManager().createQuery(hql.toString(), Map.class);

        if (limit > 0)
            query.setMaxResults(limit);

        if (parameters != null)
            parameters.forEach(query::setParameter);

        List<Map<String, L>> list = query.getResultList();

        if (list == null || list.isEmpty())
            return Optional.empty();

        return Optional.of(list);
    }

    default Optional<List<T>> getResultList(StringBuilder hql) {
        return getResultList(hql, null, 0, getEntityClass());
    }

    default Optional<List<T>> getResultList(StringBuilder hql, Map<String, Object> parameters) {
        return getResultList(hql, parameters, 0, getEntityClass());
    }

    default Optional<List<T>> getResultList(StringBuilder hql, Map<String, Object> parameters, int limit) {
        return getResultList(hql, parameters, limit, getEntityClass());
    }

    default <N> Optional<List<N>> getResultList(StringBuilder hql, Class<N> nClass) {
        return getResultList(hql, null, 0, nClass);
    }

    default <N> Optional<List<N>> getResultList(StringBuilder hql, Map<String, Object> parameters, Class<N> nClass) {
        return getResultList(hql, parameters, 0, nClass);
    }

    default <N> Optional<List<N>> getResultList(StringBuilder hql, Map<String, Object> parameters,
                                                int limit, Class<N> nClass) {
        final TypedQuery<N> query = getEntityManager().createQuery(hql.toString(), nClass);

        if (limit > 0)
            query.setMaxResults(limit);

        if (parameters != null)
            parameters.forEach(query::setParameter);

        final List<N> list = query.getResultList();

        if (list == null || list.isEmpty())
            return Optional.empty();

        return Optional.of(list);
    }
}
