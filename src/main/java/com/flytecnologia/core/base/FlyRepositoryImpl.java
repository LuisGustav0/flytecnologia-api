package com.flytecnologia.core.base;

import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyAutoCompleteFilter;
import com.flytecnologia.core.search.FlyPageableResult;
import com.flytecnologia.core.search.FlyResume;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoRepositoryBean
public abstract class FlyRepositoryImpl<T extends FlyEntity> {

    private Class<T> entityClass;

    private EntityManager entityManager;

    public FlyRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public String getEntityName() {
        return getEntityClass().getSimpleName();
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
                                                Pageable pageable, StringBuilder hql,
                                                StringBuilder hqlFrom,
                                                StringBuilder hqlOrderBy, Map<String, Object> filters) {
        Long total = getTotalRecords(hqlFrom, filters);

        hqlFrom.append(" ").append(hqlOrderBy);

        TypedQuery<?> query = getEntityManager().createQuery(hql.append(hqlFrom).toString(), resumeClass);

        if (filters != null)
            filters.forEach((label, value) -> query.setParameter(label, value));

        if (pageable.getPageNumber() != 99999998)
            addPaginationInfo(query, pageable);

        List<?> list = query.getResultList();

        return new FlyPageableResult(list,
                pageable.getPageNumber() != 99999998 ? pageable.getPageNumber() : 0,
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

    public T getReference(Long id) {
        return getEntityManager().getReference(getEntityClass(), id);
    }

    protected void changeWhereAutocomplete(StringBuilder hql, Map<String, Object> filters,
                                           FlyAutoCompleteFilter acFilter, Map<String, Object> params) {

    }

    public List<Map<String, Object>> getItensAutocomplete(FlyAutoCompleteFilter acFilter, Map<String, Object> params) {
        if (isEmpty(acFilter.getValue()))
            return null;

        StringBuilder hql = new StringBuilder()
                .append("select new Map( \n ")
                .append(acFilter.getFieldValue()).append(" as ").append(acFilter.getFieldValue()).append(", \n ")
                .append(acFilter.getFieldDescription()).append(" as ").append(acFilter.getFieldDescription()).append(" \n ");

        if (!"id".equals(acFilter.getFieldValue())) {
            hql.append(",id \n ");
        }

        if (!isEmpty(acFilter.getExtraFieldsAutocomplete())) {
            String[] extraField = acFilter.getExtraFieldsAutocomplete().split(",");

            for (String field : extraField) {
                hql.append(",").append(field).append(" as ").append(field).append(" \n ");
            }
        }

        hql
                .append(") from \n ")
                .append(getEntityClass().getSimpleName()).append(" \n ")
                .append("where \n ")
                .append("   fly_to_ascii(lower(")
                .append(acFilter.getFieldDescription())
                .append(")) like fly_to_ascii(:value) \n ");

        Map<String, Object> filters = new HashMap<>();
        changeWhereAutocomplete(hql, filters, acFilter, params);

        filters.put("value", "%" + acFilter.getValue().toLowerCase() + "%");

        TypedQuery<?> query = getEntityManager().createQuery(hql.toString(), Map.class);

        hql.append("limit ").append(acFilter.getLimit());

        filters.forEach((label, value) -> query.setParameter(label, value));

        return (List<Map<String, Object>>) query.getResultList();
    }
}