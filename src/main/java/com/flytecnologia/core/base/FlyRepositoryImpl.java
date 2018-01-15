package com.flytecnologia.core.base;

import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyPageableResult;
import com.flytecnologia.core.search.FlyResume;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

public abstract class FlyRepositoryImpl<T extends FlyEntity> implements FlyRepository<T> {
    private Class<T> entityClass;
    private EntityManager entityManager;

    public FlyRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public Class<T> getEntityClass() {
        if (entityClass != null)
            return entityClass;

        ParameterizedType parameterizedType = (ParameterizedType) getClass()
                .getGenericSuperclass();

        this.entityClass = (Class<T>) parameterizedType.getActualTypeArguments()[0];

        return entityClass;
    }

    private void addPaginationInfo(TypedQuery<?> query, Pageable pageable) {
        int actualPage = pageable.getPageNumber();
        int qtdRecordsPerPage = pageable.getPageSize();
        int firtRecordOfPage = actualPage * qtdRecordsPerPage;

        query.setFirstResult(firtRecordOfPage);
        query.setMaxResults(qtdRecordsPerPage);
    }

    protected FlyPageableResult getMapOfResults(Class<? extends FlyResume> resumeClass,
                                                Pageable pageable, StringBuilder hql, StringBuilder hqlFrom,
                                                StringBuilder hqlOrderBy, Map<String, Object> filters) {
        Long total = getTotalRecords(hqlFrom, filters);

        hqlFrom.append(" ").append(hqlOrderBy);

        TypedQuery<?> query = getEntityManager().createQuery(hql.append(hqlFrom).toString(), resumeClass);

        if (filters != null)
            filters.forEach((label, value) -> query.setParameter(label, value));

        addPaginationInfo(query, pageable);

        List<?> list = query.getResultList();

        return new FlyPageableResult(list,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                total,
                list.size());
    }

    private Long getTotalRecords(StringBuilder hqlFrom,
                                 Map<String, Object> filters) {
        String hqlCount = "select count(*) as qtd " + hqlFrom;
        Query q = getEntityManager().createQuery(hqlCount, Long.class);

        if (filters != null)
            filters.forEach((label, value) -> q.setParameter(label, value));

        return (Long) q.getSingleResult();
    }

    protected boolean isNotEmpty(Object value) {
        return !isEmpty(value);
    }

    protected boolean isEmpty(Object value) {
        return StringUtils.isEmpty(value);
    }

    protected boolean isTrue(Boolean value) {
        return value != null && value;
    }

    protected boolean isFalse(Boolean value) {
        return value != null && !value;
    }
}
