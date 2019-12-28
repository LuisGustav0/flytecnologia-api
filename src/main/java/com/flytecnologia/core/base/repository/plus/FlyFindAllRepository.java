package com.flytecnologia.core.base.repository.plus;

import com.flytecnologia.core.base.service.plus.FlyEntityClassService;
import com.flytecnologia.core.model.FlyEntity;
import lombok.NonNull;
import org.hibernate.Session;

import javax.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.flytecnologia.core.base.service.plus.FlyValidateEmptyService.isEmpty;

public interface FlyFindAllRepository<T extends FlyEntity> extends
        FlyFindByInstructionRepository<T>,
        FlyEntityClassService<T> {

    default Optional<List<T>> findAll(String tenant) {
        return findAll(null, null, getEntityClass(), tenant, false);
    }

    default Optional<List<T>> findAll(String columnReference, Object value) {
        return findAll(columnReference, value, getEntityClass(), null);
    }

    default <N> Optional<List<N>> findAll(String columnReference,
                                          Object value, Class<?> nClass) {
        return findAll(columnReference, value, nClass, null);
    }

    default Optional<List<T>> findAll(String columnReference, Object value, String tenant) {
        return findAll(columnReference, value, getEntityClass(), tenant);
    }

    default <N> Optional<List<N>> findAll(String columnReference,
                                          Object value, Class<?> nClass, String tenant) {
        return findAll(columnReference, value, nClass, tenant, true);
    }

    default <N> Optional<List<N>> findAll(String columnReference,
                                          Object value,
                                          Class<?> nClass,
                                          String tenant,
                                          boolean isColumnReferenceRequired) {

        if (isColumnReferenceRequired && isEmpty(columnReference)) {
            return Optional.empty();
        }

        final String entityName = nClass.getSimpleName();

        final StringBuilder hql = new StringBuilder()
                .append("select \n ")
                .append("   r \n")
                .append("from ").append(entityName)
                .append(" r\n");

        Map<String, Object> parameter = new HashMap<>();

        if (columnReference != null) {
            hql.append("where \n ").append(columnReference);

            if (!isEmpty(value)) {
                hql.append(" = :value");

                parameter.put("value", value);
            } else {
                hql.append(" is null");
            }
        }

        return findAllByInstruction(hql, parameter, nClass, tenant);
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
            rollbackSessionTransaction(session);
            throw e;
        }
    }
}
