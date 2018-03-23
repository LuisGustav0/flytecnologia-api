package com.flytecnologia.core.base;

import com.flytecnologia.core.exception.BusinessException;
import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.model.FlyEntityWithInactive;
import com.flytecnologia.core.search.FlyFilter;
import com.flytecnologia.core.search.FlyPageableResult;
import com.flytecnologia.core.user.FlyUserDetailsService;
import com.flytecnologia.core.util.FlyString;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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
        return getMapOfResults(pageable, hql, hqlFrom, hqlOrderBy, filters, filter, null);
    }

    protected FlyPageableResult getMapOfResults(Pageable pageable, StringBuilder hql,
                                                StringBuilder hqlFrom,
                                                StringBuilder hqlOrderBy, Map<String, Object> filters,
                                                F filter, String distinctPropertyCount) {
        filter.setAutoComplete(false);

        changeSearchWhere(hqlFrom, filters, filter);

        Long total = getTotalRecords(hqlFrom, filters, distinctPropertyCount);

        hqlFrom.append(" ").append(hqlOrderBy);

        hql.append(hqlFrom);

        TypedQuery<?> query = getEntityManager().createQuery(hql.toString(), getEntityClass());

        if (filters != null)
            filters.forEach(query::setParameter);

        if (pageable.getPageNumber() != 99999998)
            addPaginationInfo(query, pageable);

        List<?> list = query.getResultList();

        return new FlyPageableResult(list,
                pageable.getPageNumber() != 99999998 ? pageable.getPageNumber() : 0,
                pageable.getPageSize(),
                total,
                list.size());
    }

    private Long getTotalRecords(StringBuilder hqlFrom, Map<String, Object> filters, String distinctPropertyCount) {
        distinctPropertyCount = distinctPropertyCount != null ? " distinct " + distinctPropertyCount : "*";

        String hqlCount = "select count(" + distinctPropertyCount + ") as qtd " + hqlFrom.toString();
        Query q = getEntityManager().createQuery(hqlCount, Long.class);

        if (filters != null)
            filters.forEach(q::setParameter);

        Long total = (Long) q.getResultList().get(0);

        if (total == null)
            return 0L;

        return total;
    }

    public boolean isNotEmpty(Object value) {
        return !isEmpty(value);
    }

    public boolean isEmpty(Object value) {
        if (value == null)
            return true;

        if (value instanceof Collection) {
            return ((Collection) value).isEmpty();
        }

        return StringUtils.isEmpty(value) || "undefined".equals(value) || "null".equals(value);
    }

    protected boolean isTrue(Boolean value) {
        return value != null && value;
    }

    protected boolean isFalse(Boolean value) {
        return value != null && !isTrue(value);
    }

    public T getReference(Long id) {
        return getEntityManager().getReference(getEntityClass(), id);
    }

    protected void changeSearchWhere(StringBuilder hqlFrom, Map<String, Object> filters, F filter) {
    }

    protected void notNull(Object object, String message) {
        if (object == null) {
            throw new BusinessException(message);
        }
    }

    public Optional<List> getItensAutocomplete(F filter) {
        if (isEmpty(filter.getAcValue()))
            return Optional.empty();

        notNull(filter.getAcFieldValue(), "fieldValue is required");
        notNull(filter.getAcFieldDescription(), "fieldDescription is required");

        String alias = FlyString.decapitalizeFirstLetter(getEntityClass().getSimpleName());

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
                .append(getEntityClass().getSimpleName()).append(" as ")
                .append(alias).append(" \n")
                .append("where \n ")
                .append("   (fly_to_ascii(lower(")
                .append(filter.getAcFieldDescription())
                .append(")) like fly_to_ascii(:value) or \n ")
                .append("   CONCAT(").append(filter.getAcFieldValue()).append(", '') = :valueId) \n ");

        String fieldInactive = alias + ".inactive";

        if (this.getEntityClass().getGenericSuperclass().equals(FlyEntityWithInactive.class)) {
            hql.append(" and ").append(fieldInactive).append(" is false \n");
        }

        Map<String, Object> filters = new HashMap<>();

        filter.setAutoComplete(true);

        filters.put("value", "%" + filter.getAcValue().toLowerCase() + "%");
        filters.put("valueId", filter.getAcValue());

        changeSearchWhere(hql, filters, filter);

        TypedQuery<?> query = getEntityManager().createQuery(hql.toString(), Map.class);
        query.setMaxResults(filter.getAcLimit());

        filters.forEach(query::setParameter);

        return Optional.ofNullable(query.getResultList());
    }

    public Optional<Map> getItemAutocomplete(F filter) {
        if (isEmpty(filter.getId()))
            return Optional.empty();

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
                .append(getEntityClass().getSimpleName()).append(" as ")
                .append(FlyString.decapitalizeFirstLetter(getEntityClass().getSimpleName())).append(" \n")
                .append("where \n ")
                .append(filter.getAcFieldValue())
                .append(" = :id\n ");

        Map<String, Object> filters = new HashMap<>();

        filter.setAutoComplete(true);

        filters.put("id", filter.getId());

        //If it is necessary to load the record, it does not matter whether it is inactive or not
        filter.setIgnoreInactiveFilter(true);

        changeSearchWhere(hql, filters, filter);

        TypedQuery<Map> query = getEntityManager().createQuery(hql.toString(), Map.class);
        query.setMaxResults(1);

        filters.forEach(query::setParameter);

        return query.getResultList().stream().filter(Objects::nonNull).findFirst();
    }

    public FlyPageableResult search(F filter, Pageable pageable) {
        return null;
    }

    public Optional<Long> getFirstId(F filter) {
        return getPreviousNextId(filter, "min", "=", null);
    }

    public Optional<Long> getLastId(F filter) {
        return getPreviousNextId(filter, "max", "=", null);
    }

    private Optional<Long> getPreviousNextId(F filter, String maxMin, String signal, String orderByType) {
        String entityName = getEntityName();
        String alias = entityName.substring(0, 1).toLowerCase() + entityName.substring(1);

        Map<String, Object> filters = new HashMap<>();

        StringBuilder hql = new StringBuilder()
                .append("select ").append(maxMin).append("(id) from ")
                .append(getEntityName()).append(" ").append(alias)
                .append(" where 1=1 ");

        if (isEmpty(maxMin)) {
            hql.append(" and id ").append(signal).append(" :id ");
            filters.put("id", filter.getId());
        }

        changeSearchWhere(hql, filters, filter);

        if (!isEmpty(filter.getEntityDetailProperty())) {
            hql.append(" and ").append(alias).append(".").append(filter.getEntityDetailProperty())
                    .append(".id = :idDetail");
            filters.put("idDetail", filter.getMasterDetailId());
        }

        if (orderByType != null) {
            hql.append(" order by id ").append(orderByType);
        }

        TypedQuery<Long> query = getEntityManager().createQuery(hql.toString(), Long.class);
        query.setMaxResults(1);

        filters.forEach(query::setParameter);

        return query.getResultList().stream().filter(Objects::nonNull).findFirst();
    }

    public Optional<Long> getPreviousId(F filter) {
        return getPreviousNextId(filter, "", "<", "desc");
    }

    public Optional<Long> getNextId(F filter) {
        return getPreviousNextId(filter, "", ">", "asc");
    }

    public Long getUserId() {
        return FlyUserDetailsService.getCurrentUserId();
    }
}