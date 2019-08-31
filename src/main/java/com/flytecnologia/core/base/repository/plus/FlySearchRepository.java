package com.flytecnologia.core.base.repository.plus;

import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;
import com.flytecnologia.core.search.FlyPageableResult;
import org.hibernate.Session;
import org.springframework.data.domain.Pageable;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface FlySearchRepository<T extends FlyEntity, F extends FlyFilter> extends
        FlyHibernateSessionRepository,
        FlyCreateQueryRepository<T> {

    default void changeSearchWhere(StringBuilder hqlWhere, Map<String, Object> parameters, F filter) {
    }

    default void changeSearchJoin(StringBuilder hqlJoin, Map<String, Object> parameters, F filter) {

    }

    default FlyPageableResult search(F filter, Pageable pageable) {
        return null;
    }

    default FlyPageableResult getMapOfResults(Pageable pageable, StringBuilder hql,
                                              StringBuilder hqlFrom,
                                              StringBuilder hqlWhere,
                                              StringBuilder hqlOrderBy,
                                              Map<String, Object> parameters,
                                              F filter, String distinctPropertyCount) {
        if (hqlWhere == null)
            hqlWhere = new StringBuilder("\nwhere 1=1\n");

        filter.setAutoComplete(false);

        StringBuilder hqlJoin = new StringBuilder();
        changeSearchJoin(hqlJoin, parameters, filter);
        changeSearchWhere(hqlWhere, parameters, filter);

        hqlFrom.append("\n").append(hqlJoin).append("\n").append(hqlWhere);

        final Long total = getTotalRecords(hqlFrom, parameters, distinctPropertyCount);

        if (total == 0) {
            return new FlyPageableResult(new ArrayList<>(),
                    pageable != null && !filter.isShowAllRecordsOnSearch() ? pageable.getPageNumber() : 0,
                    pageable != null ? pageable.getPageSize() : -1,
                    total,
                    0);
        }

        hqlFrom.append(" ").append(hqlOrderBy);

        hql.append(hqlFrom);

        final Session session = getNewSession(filter.getTenantSearch());

        try {
            final TypedQuery<?> query = createTypedQuery(hql.toString(), getEntityClass(), session);

            if (parameters != null)
                parameters.forEach(query::setParameter);

            if (pageable != null && !filter.isShowAllRecordsOnSearch())
                addPaginationInfo(query, pageable);

            List<?> list = query.getResultList();

            closeSession(session);

            return new FlyPageableResult(list,
                    pageable != null && !filter.isShowAllRecordsOnSearch() ? pageable.getPageNumber() : 0,
                    pageable != null ? pageable.getPageSize() : -1,
                    total,
                    list != null ? list.size() : 0);

        } catch (Exception e) {
            e.printStackTrace();
            rollbackSessionTransaction(session);
            throw new RuntimeException(e.getMessage());
        }
    }

    default Long getTotalRecords(StringBuilder hqlFrom, Map<String, Object> filters, String distinctPropertyCount) {
        distinctPropertyCount = distinctPropertyCount != null ? " distinct " + distinctPropertyCount : "*";

        final String hqlCount = "select count(" + distinctPropertyCount + ") as qtd " + hqlFrom.toString();
        final Query q = getEntityManager().createQuery(hqlCount, Long.class);

        if (filters != null)
            filters.forEach(q::setParameter);

        final Long total = (Long) q.getResultList().get(0);

        if (total == null)
            return 0L;

        return total;
    }

    default void addPaginationInfo(TypedQuery<?> query, Pageable pageable) {
        final int actualPage = pageable.getPageNumber();
        final int qtdRecordsPerPage = pageable.getPageSize();
        final int firtRecordOfPage = actualPage * qtdRecordsPerPage;

        query.setFirstResult(firtRecordOfPage);
        query.setMaxResults(qtdRecordsPerPage);
    }
}
