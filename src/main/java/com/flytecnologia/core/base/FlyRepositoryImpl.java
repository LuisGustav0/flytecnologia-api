package com.flytecnologia.core.base;

import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.model.FlyEntityWithInactive;
import com.flytecnologia.core.search.FlyFilter;
import com.flytecnologia.core.search.FlyPageableResult;
import com.flytecnologia.core.user.FlyUserDetailsService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@NoRepositoryBean
public abstract class FlyRepositoryImpl<T extends FlyEntity, F extends FlyFilter>
        implements FlyValidationBase {
    //private static final Logger logger = LogManager.getLogger(FlyRepositoryImpl.class);

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

        Long total = getTotalRecords(hqlFrom, parameters, distinctPropertyCount);

        hqlFrom.append(" ").append(hqlOrderBy);

        hql.append(hqlFrom);

        TypedQuery<?> query = getEntityManager().createQuery(hql.toString(), getEntityClass());

        if (parameters != null)
            parameters.forEach(query::setParameter);

        if (pageable != null && pageable.getPageNumber() != 99999999)
            addPaginationInfo(query, pageable);

        List<?> list = query.getResultList();

        return new FlyPageableResult(list,
                pageable != null && pageable.getPageNumber() != 99999999 ? pageable.getPageNumber() : 0,
                pageable != null ? pageable.getPageSize() : -1,
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

    public Map<String, String> findImageById(Long id, String field) {
        if (isEmpty(id) || isEmpty(field))
            return null;

        String hqlCount = "select  " + field + " from " + getEntityName() + " entity where entity.id = :id ";

        Query q = getEntityManager().createQuery(hqlCount, String.class);
        q.setParameter("id", id);

        Map<String, String> data = new HashMap<>();
        data.put(field, (String) q.getResultList().get(0));

        return data;
    }

    public T getReference(Long id) {
        return getEntityManager().getReference(getEntityClass(), id);
    }

    protected void changeSearchWhere(StringBuilder hqlWhere, Map<String, Object> parameters, F filter) {
    }

    protected void changeSearchJoin(StringBuilder hqlJoin, Map<String, Object> parameters, F filter) {

    }

    public Optional<List> getItemsAutocomplete(F filter) {
        if (isEmpty(filter.getAcValue()))
            return Optional.empty();

        notNull(filter.getAcFieldValue(), "fieldValue is required");
        notNull(filter.getAcFieldDescription(), "fieldDescription is required");

        String entityName = getEntityName();
        String alias = entityName.substring(0, 1).toLowerCase() + entityName.substring(1);

        StringBuilder hql = new StringBuilder()
                .append("select distinct new Map( \n ")
                .append(alias).append(".").append(filter.getAcFieldValue()).append(" as ").append(filter.getAcFieldValue()).append(", \n ")
                .append(alias).append(".").append(filter.getAcFieldDescription()).append(" as ").append(filter.getAcFieldDescription()).append(" \n ");

        if (!"id".equals(filter.getAcFieldValue())) {
            hql.append(",").append(alias).append(".id \n ");
        }

        if (!isEmpty(filter.getAcExtraFieldsAutocomplete())) {
            String[] extraField = filter.getAcExtraFieldsAutocomplete().split(",");

            for (String field : extraField) {
                hql.append(",").append(alias).append(".").append(field).append(" as ").append(field).append(" \n ");
            }
        }

        Map<String, Object> parameters = new HashMap<>();
        StringBuilder hqlJoin = new StringBuilder();
        changeSearchJoin(hqlJoin, parameters, filter);

        hql
                .append(") from \n ")
                .append(entityName).append(" as ")
                .append(alias).append(" \n")
                .append(hqlJoin).append(" \n")
                .append("where \n ")
                .append("   (fly_to_ascii(lower(")
                .append(alias).append(".").append(filter.getAcFieldDescription())
                .append(")) like fly_to_ascii(:value) or \n ")
                .append("   CONCAT(").append(alias).append(".").append(filter.getAcFieldValue()).append(", '') = :valueId) \n ");

        String fieldInactive = alias + ".inactive";

        if (this.getEntityClass().getGenericSuperclass().equals(FlyEntityWithInactive.class)) {
            hql.append(" and ").append(fieldInactive).append(" is false \n");
        }

        filter.setAutoComplete(true);

        parameters.put("value", "%" + filter.getAcValue().toLowerCase() + "%");
        parameters.put("valueId", filter.getAcValue());

        changeSearchWhere(hql, parameters, filter);

        TypedQuery<?> query = getEntityManager().createQuery(hql.toString(), Map.class);
        query.setMaxResults(filter.getAcLimit());

        parameters.forEach(query::setParameter);

        return Optional.ofNullable(query.getResultList());
    }

    public Optional<Map> getItemAutocomplete(F filter) {
        if (isEmpty(filter.getId()))
            return Optional.empty();

        notNull(filter.getAcFieldValue(), "fieldValue is required");
        notNull(filter.getAcFieldDescription(), "fieldDescription is required");

        String entityName = getEntityName();
        String alias = entityName.substring(0, 1).toLowerCase() + entityName.substring(1);

        StringBuilder hql = new StringBuilder()
                .append("select distinct new Map( \n ")
                .append(alias).append(".").append(filter.getAcFieldValue()).append(" as ").append(filter.getAcFieldValue()).append(", \n ")
                .append(alias).append(".").append(filter.getAcFieldDescription()).append(" as ").append(filter.getAcFieldDescription()).append(" \n ");

        if (!"id".equals(filter.getAcFieldValue())) {
            hql.append(",").append(alias).append(".id \n ");
        }

        if (!isEmpty(filter.getAcExtraFieldsAutocomplete())) {
            String[] extraField = filter.getAcExtraFieldsAutocomplete().split(",");

            for (String field : extraField) {
                hql.append(",").append(alias).append(".").append(field).append(" as ").append(field).append(" \n ");
            }
        }

        Map<String, Object> parameters = new HashMap<>();
        StringBuilder hqlJoin = new StringBuilder();
        changeSearchJoin(hqlJoin, parameters, filter);

        hql
                .append(") from \n ")
                .append(entityName).append(" as ").append(alias).append(" \n")
                .append(hqlJoin).append(" \n")
                .append("where \n ")
                .append(alias).append(".").append(filter.getAcFieldValue())
                .append(" = :id\n ");

        filter.setAutoComplete(true);

        parameters.put("id", filter.getId());

        //If it is necessary to load the record, it does not matter whether it is inactive or not
        filter.setIgnoreInactiveFilter(true);

        changeSearchWhere(hql, parameters, filter);

        TypedQuery<Map> query = getEntityManager().createQuery(hql.toString(), Map.class);
        query.setMaxResults(1);

        parameters.forEach(query::setParameter);

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

        Map<String, Object> parameters = new HashMap<>();
        StringBuilder hqlJoin = new StringBuilder();
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

    public Optional<Long> getPreviousId(F filter) {
        return getPreviousNextId(filter, "", "<", "desc");
    }

    public Optional<Long> getNextId(F filter) {
        return getPreviousNextId(filter, "", ">", "asc");
    }

    public Long getUserId() {
        return FlyUserDetailsService.getCurrentUserId();
    }

    protected void addInactiveFilter(F filter, StringBuilder hqlWhere, String entityName) {
        if (!filter.isIgnoreInactiveFilter()) {
            hqlWhere.append("   and ").append(entityName).append(".inactive is ").append(filter.getInactive()).append("\n");
        }
    }

    public void detach(FlyEntity entity) {
        getEntityManager().detach(entity);
    }
}