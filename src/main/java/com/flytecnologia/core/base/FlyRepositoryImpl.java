package com.flytecnologia.core.base;

import com.flytecnologia.core.base.plusService.FlyTenantInformation;
import com.flytecnologia.core.base.plusService.FlyValidationBase;
import com.flytecnologia.core.hibernate.multitenancy.FlyMultiTenantConstants;
import com.flytecnologia.core.hibernate.multitenancy.FlyTenantThreadLocal;
import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.model.FlyEntityWithInactiveImpl;
import com.flytecnologia.core.search.FlyFilter;
import com.flytecnologia.core.search.FlyPageableResult;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@NoRepositoryBean
public abstract class FlyRepositoryImpl<T extends FlyEntity, F extends FlyFilter>
        implements FlyValidationBase, FlyTenantInformation {
    //private static final Logger logger = LogManager.getLogger(FlyRepositoryImpl.class);

    private Class<T> entityClass;

    private EntityManager entityManager;
    private EntityManagerFactory entityManagerFactory;

    public FlyRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public FlyRepositoryImpl(EntityManager entityManager, EntityManagerFactory entityManagerFactory) {
        this.entityManager = entityManager;
        this.entityManagerFactory = entityManagerFactory;
    }

    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return this.entityManagerFactory;
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

        if (pageable != null && !filter.isShowAllRecordsOnSearch())
            addPaginationInfo(query, pageable);

        List<?> list = query.getResultList();

        return new FlyPageableResult(list,
                pageable != null && !filter.isShowAllRecordsOnSearch() ? pageable.getPageNumber() : 0,
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

    /*fly-input-image-upload*/
    public Map<String, String> findImageById(Long id, String field) {
        Optional<String> value = getFieldById(id, field);

        Map<String, String> data = new HashMap<>();
        data.put(field, value.orElse(null));

        return data;
    }

    public <T> Optional<T> getFieldById(Long id, String property) {
        if (isEmpty(property) || isEmpty(id)) {
            return Optional.empty();
        }

        property = "p." + property;

        String hql = "select " + property + " from " + getEntityName() + " p where p.id = :id";

        return getEntityManager().createQuery(hql)
                .setParameter("id", id)
                .setMaxResults(1)
                .getResultList()
                .stream()
                .filter(Objects::nonNull)
                .findFirst();
    }


    public Optional<T> getReference(Long id) {
        if (isEmpty(id))
            return Optional.empty();

        T entity = getEntityManager().getReference(getEntityClass(), id);

        if (entity == null)
            return Optional.empty();

        return Optional.of(entity);
    }

    public Optional<T> find(Long id) {
        if (isEmpty(id))
            return Optional.empty();

        T entity = getEntityManager().find(getEntityClass(), id);

        if (entity == null)
            return Optional.empty();

        return Optional.of(entity);
    }

    protected void changeSearchWhere(StringBuilder hqlWhere, Map<String, Object> parameters, F filter) {
    }

    protected void changeSearchJoin(StringBuilder hqlJoin, Map<String, Object> parameters, F filter) {

    }

    public Optional<List<Map<String, Object>>> getItemsAutocomplete(F filter) {
        if (isEmpty(filter.getAcValue()))
            return Optional.empty();

        notNull(filter.getAcFieldValue(), "fieldValue is required");
        notNull(filter.getAcFieldDescription(), "fieldDescription is required");

        String entityName = getEntityName();
        String alias = entityName.substring(0, 1).toLowerCase() + entityName.substring(1);

        StringBuilder hql = new StringBuilder()
                .append("select distinct new Map( \n ")
                .append(alias).append(".").append(filter.getAcFieldValue()).append(" as ").append(filter.getAcFieldValue()).append("\n ");

        addFieldDescriptionToListAutocomplete(filter, alias, hql);

        addFieldIdToAutocomplete(filter, alias, hql);

        addExtraFieldsToAutocomplete(filter, alias, hql);

        Map<String, Object> parameters = new HashMap<>();
        StringBuilder hqlJoin = new StringBuilder();
        changeSearchJoin(hqlJoin, parameters, filter);

        hql
                .append(") from \n ")
                .append(entityName).append(" as ")
                .append(alias).append(" \n")
                .append(hqlJoin).append(" \n")
                .append("where (\n ");

        addFieldDescriptionToWhereAutocomplete(filter, alias, hql);

        hql.append(" OR CONCAT(").append(alias).append(".").append(filter.getAcFieldValue()).append(", '') = :valueId) \n ");

        String fieldInactive = alias + ".inactive";

        if (this.getEntityClass().getGenericSuperclass().equals(FlyEntityWithInactiveImpl.class)) {
            hql.append(" and ").append(fieldInactive).append(" is false \n");
        }

        filter.setAutoComplete(true);

        parameters.put("value", "%" + filter.getAcValue().toLowerCase() + "%");
        parameters.put("valueId", filter.getAcValue());

        changeSearchWhere(hql, parameters, filter);

        return getListMap(hql, parameters, filter.getAcLimit());
    }

    private void addFieldIdToAutocomplete(F filter, String alias, StringBuilder hql) {
        if (!"id".equals(filter.getAcFieldValue())) {
            hql.append(",").append(alias).append(".id \n ");
        }
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
                .append(alias).append(".").append(filter.getAcFieldValue())
                .append(" as ").append(filter.getAcFieldValue());

        addFieldDescriptionToListAutocomplete(filter, alias, hql);

        addFieldIdToAutocomplete(filter, alias, hql);

        addExtraFieldsToAutocomplete(filter, alias, hql);

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

        Map<String, Object> map = query.getResultList().stream().filter(Objects::nonNull).findFirst().orElse(null);

        if (map == null) {
            return Optional.empty();
        }

        formatMapItemAutocomplete(alias, map);

        return Optional.of(map);
    }

    private void formatMapItemAutocomplete(String alias, Map<String, Object> map) {
        Set<String> keys = map.keySet();

        Iterator<String> it = keys.iterator();

        Map<String, Object> mapAux = new HashMap<>();

        while (it.hasNext()) {
            String key = it.next();

            if (key.contains("$")) {
                Object value = map.get(key);
                //map.remove(key);

                String[] children = key.split("\\$");

                if (children[0].equals(alias)) {
                    children = ArrayUtils.removeElement(children, children[0]);
                }

                for (int x = 0; x < children.length; x++) {
                    String child = children[x];

                    if (x < children.length - 1) {
                        if (x == 0) {
                            mapAux.put(child, new HashMap<>());
                        } else {
                            ((Map) mapAux.get(children[x - 1])).put(child, new HashMap<>());
                        }
                    } else {
                        ((Map) mapAux.get(children[x - 1])).put(child, value);
                    }
                }

            }
        }

        map.putAll(mapAux);
    }

    protected void addExtraFieldsToAutocomplete(F filter, String alias, StringBuilder hql) {
        if (!isEmpty(filter.getAcExtraFieldsAutocomplete())) {
            String[] extraField = filter.getAcExtraFieldsAutocomplete().split(",");

            for (String field : extraField) {
                hql.append(",");

                addExtraFieldsToAutocomplete(field, alias, hql);
            }
        }
    }

    protected void addExtraFieldsToAutocomplete(String field, String alias, StringBuilder hql) {
        if (!field.contains(".")) {
            hql.append(alias)
                    .append(".")
                    .append(field.trim())
                    .append(" as ").append(field.trim()).append(" \n ");
        } else {
            hql.append(field.trim()).append(" as ").append(field.trim().replace(".", "$")).append(" \n ");
        }
    }

    private void addFieldDescriptionToListAutocomplete(F filter, String alias, StringBuilder hql) {
        if (isEmpty(filter.getAcFieldsListAutocomplete())) {
            hql.append(",")
                    .append(alias)
                    .append(".")
                    .append(filter.getAcFieldDescription().replace("__", "."))
                    .append(" as ")
                    .append(filter.getAcFieldDescription())
                    .append(" \n ");
        } else {
            String[] extraField = filter.getAcFieldsListAutocomplete().split(",");

            hql.append(",CONCAT(");

            int count = 0;

            for (String field : extraField) {
                if (!field.contains("."))
                    hql.append(alias).append(".");

                hql.append(field.trim());

                if (count < extraField.length - 1) {
                    hql.append(", ' - ', ");
                }

                count++;
            }

            hql.append(") as ").append(filter.getAcFieldDescription()).append(" \n ");
        }
    }

    private void addFieldDescriptionToWhereAutocomplete(F filter, String alias, StringBuilder hql) {
        if (isEmpty(filter.getAcFieldsListAutocomplete())) {
            hql.append("   fly_to_ascii(lower(")
                    .append(alias)
                    .append(".")
                    .append(filter.getAcFieldDescription().replace("__", "."))
                    .append(")) like fly_to_ascii(:value) \n ");
        } else {
            String[] extraField = filter.getAcFieldsListAutocomplete().split(",");

            int count = 0;

            hql.append("(");

            for (String field : extraField) {
                if (count > 0) {
                    hql.append(" OR ");
                }

                hql.append("   fly_to_ascii(lower(");

                if (!field.contains("."))
                    hql.append(alias).append(".");

                hql.append(field.trim())
                        .append(")) like fly_to_ascii(:value) \n ");

                count++;
            }

            hql.append(")");
        }
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

    public Optional<Long> getRecordListCount(Long id, String listName) {
        String entityName = getEntityName();

        StringBuilder hql = new StringBuilder()
                .append("select count(entities.id)  \nfrom  ").append(entityName).append(" super  \n")
                .append("inner join super.").append(listName).append(" as entities \n")
                .append("where super.id = :id\n");

        TypedQuery<Long> query = getEntityManager().createQuery(hql.toString(), Long.class);
        query.setParameter("id", id);

        return query.getResultList().stream().filter(Objects::nonNull).findFirst();
    }

    public Optional<Long> getPreviousId(F filter) {
        return getPreviousNextId(filter, "", "<", "desc");
    }

    public Optional<Long> getNextId(F filter) {
        return getPreviousNextId(filter, "", ">", "asc");
    }

    protected void addInactiveFilter(F filter, StringBuilder hqlWhere, String entityName) {
        if (!filter.isIgnoreInactiveFilter()) {
            if (filter.getInactive() != null) {
                hqlWhere.append("   and ").append(entityName).append(".inactive is ").append(filter.getInactive()).append("\n");
            }
        }
    }

    public <G extends FlyEntity> void detach(G entity) {
        if (entity != null)
            getEntityManager().detach(entity);
    }

    public void flush() {
        getEntityManager().flush();
    }

    protected <L> Optional<List<Map<String, L>>> getListMap(StringBuilder hql) {
        return getListMap(hql, null, 0);
    }

    protected <L> Optional<List<Map<String, L>>> getListMap(StringBuilder hql, Map<String, Object> parameters) {
        return getListMap(hql, parameters, 0);
    }

    protected <L> Optional<List<Map<String, L>>> getListMap(StringBuilder hql, Map<String, Object> parameters, int limit) {
        TypedQuery<?> query = getEntityManager().createQuery(hql.toString(), Map.class);

        if (limit > 0)
            query.setMaxResults(limit);

        if (parameters != null)
            parameters.forEach(query::setParameter);

        return Optional.ofNullable((List<Map<String, L>>) query.getResultList());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void batchSave(List<T> entities, int batchSize) {
        int entityCount = entities.size();

        EntityManager entityManager = null;
        EntityTransaction transaction = null;

        try {
            entityManager = getEntityManagerFactory().createEntityManager();

            transaction = entityManager.getTransaction();
            transaction.begin();

            for (int i = 0; i < entityCount; ++i) {
                if (i > 0 && i % batchSize == 0) {
                    entityManager.flush();
                    entityManager.clear();

                    //transaction.commit();
                    //transaction.begin();
                }

                T entity = entities.get(i);

                Map<String, Object> parameters = entity.getParameters();

                entity = entityManager.merge(entities.get(i));

                entity.setParameters(parameters);
            }

            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    public <T> Optional<T> getValue(StringBuilder hql, Long id) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", id);

        return getValue(hql, parameters);
    }

    public <T> Optional<T> getValue(StringBuilder hql, Map<String, Object> parameters) {
        Query query = getEntityManager().createQuery(hql.toString());
        parameters.forEach(query::setParameter);

        return query.getResultList().stream().filter(Objects::nonNull).findFirst();
    }

    @Transactional
    public void setTenantInCurrentConnection(String tenantIdentifier) {
        //flush();

        FlyTenantThreadLocal.setTenant(tenantIdentifier);

        if (tenantIdentifier != null) {
            tenantIdentifier = "SET search_path TO  " + tenantIdentifier;
        } else {
            tenantIdentifier = "SET search_path TO  " + FlyMultiTenantConstants.DEFAULT_TENANT_ID;
        }

        getEntityManager().createNativeQuery(tenantIdentifier).executeUpdate();

        flush();
    }
}