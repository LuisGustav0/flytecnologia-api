package com.flytecnologia.core.base.repository.plus;

import com.flytecnologia.core.model.FlyEntity;
import lombok.NonNull;
import org.hibernate.Session;

import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.flytecnologia.core.base.service.plus.FlyValidateEmptyService.isEmpty;

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
            e.printStackTrace();
            rollbackSessionTransaction(session);
            throw new RuntimeException(e.getMessage());
        }
    }

    default Optional<List<T>> findAllByInstruction(@NonNull StringBuilder hql,
                                                   Map<String, ?> parameters) {
        return findAllByInstruction(hql, parameters, getEntityClass(), null);
    }

    default Optional<List<T>> findAllByInstruction(@NonNull StringBuilder hql,
                                                   Map<String, ?> parameters,
                                                   String tenant) {
        return findAllByInstruction(hql, parameters, getEntityClass(), tenant);
    }

    default Optional<List<T>> findAllByInstruction(@NonNull String hql,
                                                   Map<String, ?> parameters) {
        return findAllByInstruction(hql, parameters, getEntityClass(), null);
    }

    default Optional<List<T>> findAllByInstruction(@NonNull String hql,
                                                   Map<String, ?> parameters,
                                                   String tenant) {
        return findAllByInstruction(hql, parameters, getEntityClass(), tenant);
    }

    default <N> Optional<List<N>> findAllByInstruction(@NonNull StringBuilder hql,
                                                       Map<String, ?> parameters,
                                                       Class<?> nClass) {
        return findAllByInstruction(hql.toString(), parameters, nClass, null);
    }


    default <N> Optional<List<N>> findAllByInstruction(@NonNull StringBuilder hql,
                                                       Map<String, ?> parameters,
                                                       Class<?> nClass,
                                                       String tenant) {
        return findAllByInstruction(hql.toString(), parameters, nClass, tenant);
    }

    default <N> Optional<List<N>> findAllByInstruction(@NonNull String hql,
                                                       Map<String, ?> parameters,
                                                       Class<?> nClass,
                                                       String tenant) {
        final Session session = getNewSession(tenant);

        try {
            final TypedQuery<?> query = createTypedQuery(hql, nClass, session);

            if (parameters != null) {
                parameters.forEach(query::setParameter);
            }

            final List<?> provider = query.getResultList();

            closeSession(session);

            if (isEmpty(provider)) {
                return Optional.empty();
            }

            return Optional.ofNullable((List<N>) provider);
        } catch (Exception e) {
            e.printStackTrace();
            rollbackSessionTransaction(session);
            throw new RuntimeException(e.getMessage());
        }
    }
}
