package com.flytecnologia.core.base.repository.plus;

import com.flytecnologia.core.model.FlyEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.flytecnologia.core.base.service.plus.FlyValidateEmptyService.isEmpty;

public interface FlyFindRepository<T extends FlyEntity> extends FlyFindByInstructionRepository<T> {
    Class<T> getEntityClass();

    String getEntityName();

    default Optional<T> find(Long id) {
        if (isEmpty(id))
            return Optional.empty();

        final T entity = getEntityManager().find(getEntityClass(), id);

        if (entity == null)
            return Optional.empty();

        return Optional.of(entity);
    }

    default Optional<T> find(Long id, String tenant) {
        if (isEmpty(id))
            return Optional.empty();

        final String entityName = getEntityName();

        final StringBuilder hql = new StringBuilder()
                .append("select \n ")
                .append("   r \n")
                .append("from ").append(entityName)
                .append(" r\n")
                .append("where \n ")
                .append("   r.id = :id");

        Map<String, Long> parameters = new HashMap<>();
        parameters.put("id", id);
        return findByInstruction(hql, parameters, tenant);
    }

    default Optional<T> find(String columnReference,
                             Object value) {
        return find(columnReference, value, null);
    }

    default Optional<T> find(String columnReference,
                             Object value,
                             String tenant) {

        if (isEmpty(columnReference))
            return Optional.empty();

        final StringBuilder hql = new StringBuilder()
                .append("select \n ")
                .append("   r \n")
                .append("from ").append(getEntityName())
                .append(" r\n");

        Map<String, Object> parameter = new HashMap<>();

        if (columnReference != null) {
            hql.append("where \n ").append(columnReference);

            if (!isEmpty(value)) {
                parameter.put("value", value);

                hql.append(" = :value");
            } else {
                hql.append(" is null");
            }
        }

        return findByInstruction(hql, parameter, getEntityClass(), tenant);
    }
}
