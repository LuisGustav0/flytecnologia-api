package com.flytecnologia.core.base.repository.plus;

import com.flytecnologia.core.model.FlyEntity;
import lombok.NonNull;
import org.hibernate.Session;

import javax.persistence.TypedQuery;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public interface FlyFindByInstructionRepository<T extends FlyEntity> extends
        FlyHibernateSessionRepository,
        FlyCreateQueryRepository<T> {

    default Optional<T> findByInstruction(@NonNull String hql) {
        return findByInstruction(hql, null, null);
    }

    default Optional<T> findByInstruction(@NonNull StringBuilder hql) {
        return findByInstruction(hql, null, null);
    }

    default Optional<T> findByInstruction(@NonNull String hql, String tenant) {
        return findByInstruction(hql, null, tenant);
    }


    default Optional<T> findByInstruction(@NonNull StringBuilder hql, String tenant) {
        return findByInstruction(hql, null, tenant);
    }

    default Optional<T> findByInstruction(@NonNull StringBuilder hql,
                                          Map<String, ?> parameters,
                                          String tenant) {
        return findByInstruction(hql.toString(), parameters, tenant);
    }

    default <N> Optional<N> findByInstruction(@NonNull StringBuilder hql,
                                              Map<String, ?> parameters,
                                              Class<?> nClass,
                                              String tenant) {
        return findByInstruction(hql.toString(), parameters, nClass, tenant);
    }

    default Optional<T> findByInstruction(@NonNull String hql,
                                          Map<String, ?> parameters,
                                          String tenant) {
        return findByInstruction(hql, parameters, getEntityClass(), tenant);
    }

    default <N> Optional<N> findByInstruction(@NonNull String hql,
                                              Map<String, ?> parameters,
                                              Class<?> nClass,
                                              String tenant) {
        final Session session = getNewSession(tenant);

        try {
            TypedQuery<?> query = createTypedQuery(hql, nClass, session);

            if (parameters != null) {
                parameters.forEach(query::setParameter);
            }

            final Optional<?> result = query
                    .setMaxResults(1)
                    .getResultList().stream().filter(Objects::nonNull).findFirst();

            closeSession(session);

            return (Optional<N>) result;
        } catch (Exception e) {
            rollbackSessionTransaction(session);
            throw e;
        }
    }
}
