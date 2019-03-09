package com.flytecnologia.core.base.repository.plus;

import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;

import javax.persistence.TypedQuery;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.flytecnologia.core.base.service.plus.FlyValidateEmptyService.isEmpty;

public interface FlyFindNextRepository<T extends FlyEntity, F extends FlyFilter> extends
        FlySearchRepository<T, F> {

    default Optional<Long> getFirstId(F filter) {
        return getPreviousNextId(filter, "min", "=", null);
    }

    default Optional<Long> getLastId(F filter) {
        return getPreviousNextId(filter, "max", "=", null);
    }

    default Optional<Long> getPreviousNextId(F filter, String maxMin, String signal, String orderByType) {
        final String entityName = getEntityName();
        final String alias = entityName.substring(0, 1).toLowerCase() + entityName.substring(1);

        final Map<String, Object> parameters = new HashMap<>();
        final StringBuilder hqlJoin = new StringBuilder();
        changeSearchJoin(hqlJoin, parameters, filter);

        StringBuilder hql = new StringBuilder()
                .append("select ").append(maxMin).append("(").append(alias).append(".id) from ")
                .append(getEntityName()).append(" ").append(alias).append("\n")
                .append(hqlJoin).append("\n")
                .append("where 1=1 \n");

        if (isEmpty(maxMin)) {
            hql.append("    and ").append(alias).append(".id ").append(signal).append(" :id \n");
            parameters.put("id", filter.getId());
        }

        filter.setIsPreviousOrNextId(true);

        changeSearchWhere(hql, parameters, filter);

        if (!isEmpty(filter.getEntityDetailProperty())) {
            hql
                    .append("    and ").append(alias).append(".").append(filter.getEntityDetailProperty())
                    .append(".id = :idDetail \n");
            parameters.put("idDetail", filter.getMasterDetailId());
        }

        if (orderByType != null) {
            hql.append(" order by ").append(alias).append(".id ").append(orderByType);
        }

        TypedQuery<Long> query = getEntityManager().createQuery(hql.toString(), Long.class);
        query.setMaxResults(1);

        parameters.forEach(query::setParameter);

        return query.getResultList().stream().filter(Objects::nonNull).findFirst();
    }

    default Optional<Long> getPreviousId(F filter) {
        return getPreviousNextId(filter, "", "<", "desc");
    }

    default Optional<Long> getNextId(F filter) {
        return getPreviousNextId(filter, "", ">", "asc");
    }

}
