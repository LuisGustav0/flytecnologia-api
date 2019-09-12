package com.flytecnologia.core.base.repository.plus;

import com.flytecnologia.core.base.service.plus.FlyEntityClassService;
import com.flytecnologia.core.model.FlyEntity;

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
}
