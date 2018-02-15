package com.flytecnologia.core.base;

import com.flytecnologia.core.exception.BusinessException;
import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;
import com.flytecnologia.core.search.FlyPageableResult;
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
public abstract class FlyRepositoryImpl<T extends FlyEntity, F extends FlyFilter> {

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

    protected FlyPageableResult getMapOfResults(Pageable pageable, StringBuilder hql,
                                                StringBuilder hqlFrom,
                                                StringBuilder hqlOrderBy, Map<String, Object> filters,
                                                F filter) {
        filter.setAutoComplete(false);
        changeSearchWhere(hqlFrom, filters, filter);

        Long total = getTotalRecords(hqlFrom, filters);

        hqlFrom.append(" ").append(hqlOrderBy);

        TypedQuery<?> query = getEntityManager().createQuery(hql.append(hqlFrom).toString(), getEntityClass());

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

    private Long getTotalRecords(StringBuilder hqlFrom, Map<String, Object> filters) {
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
        return StringUtils.isEmpty(value) || "undefined".equals(value) || "null".equals(value);
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

    protected void changeSearchWhere(StringBuilder hql, Map<String, Object> filters, F filter) {
    }

    protected void notNull(Object object, String message) {
        if (object == null) {
            throw new BusinessException(message);
        }
    }

    public List<Map<String, Object>> getItensAutocomplete(F filter) {
        if (isEmpty(filter.getAcValue()))
            return null;

        notNull(filter.getAcFieldValue(), "fieldValue is required");
        notNull(filter.getAcFieldDescription(), "fieldDescription is required");

        StringBuilder hql = new StringBuilder()
                .append("select new Map( \n ")
                .append(filter.getAcFieldValue()).append(" as ").append(filter.getAcFieldValue()).append(", \n ")
                .append(filter.getAcFieldDescription()).append(" as ").append(filter.getAcFieldDescription()).append(" \n ");

        if (!"id".equals(filter.getAcFieldValue())) {
            hql.append(",id \n ");
        }

        if (!isEmpty(filter.getAcExtraFieldsAutocomplete())) {
            String[] extraField = filter.getAcExtraFieldsAutocomplete().split(",");

            for (String field : extraField) {
                hql.append(",").append(field).append(" as ").append(field).append(" \n ");
            }
        }

        hql
                .append(") from \n ")
                .append(getEntityClass().getSimpleName()).append(" \n ")
                .append("where \n ")
                .append("   (fly_to_ascii(lower(")
                .append(filter.getAcFieldDescription())
                .append(")) like fly_to_ascii(:value) or \n ")
                .append("   CONCAT(").append(filter.getAcFieldValue()).append(", '') = :valueId) \n ");

        Map<String, Object> filters = new HashMap<>();

        filter.setAutoComplete(true);


        filters.put("value", "%" + filter.getAcValue().toLowerCase() + "%");
        filters.put("valueId", filter.getAcValue());

        TypedQuery<?> query = getEntityManager().createQuery(hql.toString(), Map.class);

        hql.append("limit ").append(filter.getAcLimit());

        filters.forEach((label, value) -> query.setParameter(label, value));

        return (List<Map<String, Object>>) query.getResultList();
    }

    public Map<String, Object> getItemAutocomplete(F filter) {
        if (isEmpty(filter.getId()))
            return null;

        notNull(filter.getAcFieldValue(), "fieldValue is required");
        notNull(filter.getAcFieldDescription(), "fieldDescription is required");

        StringBuilder hql = new StringBuilder()
                .append("select new Map( \n ")
                .append(filter.getAcFieldValue()).append(" as ").append(filter.getAcFieldValue()).append(", \n ")
                .append(filter.getAcFieldDescription()).append(" as ").append(filter.getAcFieldDescription()).append(" \n ");

        if (!"id".equals(filter.getAcFieldValue())) {
            hql.append(",id \n ");
        }

        if (!isEmpty(filter.getAcExtraFieldsAutocomplete())) {
            String[] extraField = filter.getAcExtraFieldsAutocomplete().split(",");

            for (String field : extraField) {
                hql.append(",").append(field).append(" as ").append(field).append(" \n ");
            }
        }

        hql
                .append(") from \n ")
                .append(getEntityClass().getSimpleName()).append(" \n ")
                .append("where \n ")
                .append(filter.getAcFieldValue())
                .append(" = :id\n ");

        Map<String, Object> filters = new HashMap<>();

        filter.setAutoComplete(true);

        changeSearchWhere(hql, filters, filter);

        filters.put("id", filter.getId());

        TypedQuery<?> query = getEntityManager().createQuery(hql.toString(), Map.class);

        hql.append("limit 1");

        filters.forEach((label, value) -> query.setParameter(label, value));

        return (Map<String, Object>) query.getSingleResult();
    }

    public FlyPageableResult search(F filter, Pageable pageable) {
        return null;
    }
}