package com.flytecnologia.core.base.repository.plus;

import com.flytecnologia.core.model.FlyEntity;
import lombok.NonNull;
import org.hibernate.Session;

import javax.persistence.Query;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.flytecnologia.core.base.service.plus.FlyValidateEmptyService.isEmpty;

public interface FlyFindValueRepository<T extends FlyEntity>
        extends FlyHibernateSessionRepository, FlyCreateQueryRepository<T> {
    String getEntityName();

    default <N> Optional<N> getValue(@NonNull StringBuilder hql, @NonNull Long id) {
        return getValue(hql.toString(), id, null);
    }

    default <N> Optional<N> getValue(@NonNull String hql, @NonNull Long id) {
        return getValue(hql, id, null);
    }

    default <N> Optional<N> getValue(@NonNull StringBuilder hql, @NonNull Long id, String tenant) {
        return getValue(hql.toString(), id, tenant);
    }

    default <N> Optional<N> getValue(@NonNull String hql, @NonNull Long id, String tenant) {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", id);

        return getValue(hql, parameters, tenant);
    }

    default <N> Optional<N> getValue(@NonNull String hql, Map<String, Object> parameters) {
        return getValue(hql, parameters, null);
    }

    default <N> Optional<N> getValue(@NonNull StringBuilder hql, Map<String, Object> parameters) {
        return getValue(hql, parameters, null);
    }

    default <N> Optional<N> getValue(@NonNull StringBuilder hql, Map<String, Object> parameters, String tenant) {
        return getValue(hql.toString(), parameters, tenant);
    }

    default <N> Optional<N> getValue(@NonNull String hql, Map<String, Object> parameters, String tenant) {
        final Session session = getNewSession(tenant);

        try {
            Query query = createQuery(hql, session);

            if (parameters != null) {
                parameters.forEach(query::setParameter);
            }

            final Optional<N> result = query.getResultList().stream().filter(Objects::nonNull).findFirst();

            closeSession(session);

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            rollbackSessionTransaction(session);
            throw new RuntimeException(e.getMessage());
        }
    }

    default <N> Optional<N> getFieldById(Long id, String property) {
        return getFieldById(id, property, null);
    }

    default <N> Optional<N> getFieldById(Long id, String property, String tenant) {
        if (isEmpty(property) || isEmpty(id)) {
            return Optional.empty();
        }

        property = "p." + property;

        final String hql = "select " + property + " from " + getEntityName() + " p where p.id = :id";

        return getValue(hql, id, tenant);
    }

    /*fly-input-image-upload*/
    default Map<String, String> findImageById(Long id, String field) {
        final Optional<String> value = getFieldById(id, field);

        final Map<String, String> data = new HashMap<>();
        data.put(field, value.orElse(null));

        return data;
    }
}
