package com.flytecnologia.core.base;

import com.flytecnologia.core.base.plusService.FlyTenantInformation;
import com.flytecnologia.core.base.plusService.FlyValidationBase;
import com.flytecnologia.core.hibernate.multitenancy.FlyMultiTenantConstants;
import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.model.FlyEntityWithInactiveImpl;
import com.flytecnologia.core.search.FlyFilter;
import com.flytecnologia.core.search.FlyPageableResult;
import lombok.NonNull;
import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
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

    private Class<T> entityClass;

    private EntityManager entityManager;
    private EntityManagerFactory entityManagerFactory;

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
        final int actualPage = pageable.getPageNumber();
        final int qtdRecordsPerPage = pageable.getPageSize();
        final int firtRecordOfPage = actualPage * qtdRecordsPerPage;

        query.setFirstResult(firtRecordOfPage);
        query.setMaxResults(qtdRecordsPerPage);
    }

    private String getTenantSearch(F filter) {
        String tenant = filter.getTenantSearch();

        if (isEmpty(tenant))
            return getTenant();

        return tenant;
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

        final Long total = getTotalRecords(hqlFrom, parameters, distinctPropertyCount);

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

    private Long getTotalRecords(StringBuilder hqlFrom, Map<String, Object> filters, String distinctPropertyCount) {
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

    /*fly-input-image-upload*/
    public Map<String, String> findImageById(Long id, String field) {
        final Optional<String> value = getFieldById(id, field);

        final Map<String, String> data = new HashMap<>();
        data.put(field, value.orElse(null));

        return data;
    }

    public <N> Optional<N> getFieldById(Long id, String property) {
        return getFieldById(id, property, null);
    }

    public <N> Optional<N> getFieldById(Long id, String property, String tenant) {
        if (isEmpty(property) || isEmpty(id)) {
            return Optional.empty();
        }

        property = "p." + property;

        final String hql = "select " + property + " from " + getEntityName() + " p where p.id = :id";

        return getValue(hql, id, tenant);
    }

    public Optional<T> getReference(Long id) {
        return getReference(id, null);
    }

    public Optional<T> getReference(Long id, String tenant) {
        if (isEmpty(id))
            return Optional.empty();

        final Session session = getNewSession(tenant);

        try {

            T entity;

            if (session != null) {
                entity = session.getReference(getEntityClass(), id);
            } else {
                entity = getEntityManager().getReference(getEntityClass(), id);
            }

            closeSession(session);

            if (entity == null)
                return Optional.empty();

            return Optional.of(entity);
        } catch (Exception e) {
            e.printStackTrace();
            rollbackSessionTransaction(session);
            throw new RuntimeException(e.getMessage());
        }
    }


    public Optional<T> find(Long id) {
        if (isEmpty(id))
            return Optional.empty();

        final T entity = getEntityManager().find(getEntityClass(), id);

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

        validateFiltersRequiredToAutocomplete(filter);

        final String entityName = getEntityName();
        final String alias = entityName.substring(0, 1).toLowerCase() + entityName.substring(1);

        final StringBuilder hql = new StringBuilder()
                .append("select distinct new Map(\n ")
                .append(alias).append(".").append(filter.getAcFieldValue())
                .append(" as ").append(filter.getAcFieldValue()).append("\n ");

        addFieldDescriptionToListAutocomplete(filter, alias, hql);

        addFieldIdToAutocomplete(filter, alias, hql);

        addExtraFieldsToAutocomplete(filter, alias, hql);

        final StringBuilder hqlJoin = new StringBuilder();
        final Map<String, Object> parameters = new HashMap<>();
        changeSearchJoin(hqlJoin, parameters, filter);

        hql
                .append(") from \n ")
                .append(entityName).append(" as ")
                .append(alias).append(" \n")
                .append(hqlJoin).append(" \n")
                .append("where (\n ");

        addFieldDescriptionToWhereAutocomplete(filter, alias, hql);

        hql.append(" OR CONCAT(").append(alias).append(".").append(filter.getAcFieldValue()).append(", '') = :valueId) \n ");

        final String fieldInactive = alias + ".inactive";

        if (this.getEntityClass().getGenericSuperclass().equals(FlyEntityWithInactiveImpl.class)) {
            hql.append(" and ").append(fieldInactive).append(" is false \n");
        }

        filter.setAutoComplete(true);

        parameters.put("value", "%" + filter.getAcValue().toLowerCase() + "%");
        parameters.put("valueId", filter.getAcValue());

        changeSearchWhere(hql, parameters, filter);

        return getResultListMap(hql, parameters, filter.getAcLimit());
    }

    private void addFieldIdToAutocomplete(F filter, String alias, StringBuilder hql) {
        if (!"id".equals(filter.getAcFieldValue())) {
            hql.append(",").append(alias).append(".id \n ");
        }
    }

    public Optional<Map> getItemAutocomplete(F filter) {
        if (isEmpty(filter.getId()))
            return Optional.empty();

        validateFiltersRequiredToAutocomplete(filter);

        final String entityName = getEntityName();
        final String alias = entityName.substring(0, 1).toLowerCase() + entityName.substring(1);

        final StringBuilder hql = new StringBuilder()
                .append("select distinct new Map(\n ")
                .append(alias).append(".").append(filter.getAcFieldValue())
                .append(" as ").append(filter.getAcFieldValue());

        addFieldDescriptionToListAutocomplete(filter, alias, hql);

        addFieldIdToAutocomplete(filter, alias, hql);

        addExtraFieldsToAutocomplete(filter, alias, hql);

        final Map<String, Object> parameters = new HashMap<>();
        final StringBuilder hqlJoin = new StringBuilder();
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

        final TypedQuery<Map> query = getEntityManager().createQuery(hql.toString(), Map.class);
        query.setMaxResults(1);

        parameters.forEach(query::setParameter);

        final Map<String, Object> map = query.getResultList().stream().filter(Objects::nonNull).findFirst().orElse(null);

        if (map == null) {
            return Optional.empty();
        }

        formatMapItemAutocomplete(alias, map);

        return Optional.of(map);
    }

    private void validateFiltersRequiredToAutocomplete(F filter) {
        notNull(filter.getAcFieldValue(), "fieldValue is required");
        notNull(filter.getAcFieldDescription(), "fieldDescription is required");
    }

    private void formatMapItemAutocomplete(String alias, Map<String, Object> map) {
        final Set<String> keys = map.keySet();

        final Iterator<String> it = keys.iterator();

        final Map<String, Object> mapAux = new HashMap<>();

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
            if (!filter.getAcFieldValue().equals(filter.getAcFieldDescription())) {
                hql.append(",")
                        .append(alias)
                        .append(".")
                        .append(getFormatedField(filter.getAcFieldDescription()))
                        .append(" as ")
                        .append(filter.getAcFieldDescription())
                        .append(" \n ");
            }
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
            addLikeToFieldDescription(hql, alias, filter.getAcFieldDescription());
        } else {
            String[] extraField = filter.getAcFieldsListAutocomplete().split(",");

            int count = 0;

            hql.append("(");

            for (String field : extraField) {
                if (count > 0) {
                    hql.append(" OR ");
                }

                addLikeToFieldDescription(hql, alias, field);

                count++;
            }

            hql.append(")");
        }
    }

    private String getFormatedField(@NonNull String field) {
        return field.replace("__", ".");
    }

    private void addLikeToFieldDescription(StringBuilder hql, String alias, String field) {
        field = getFormatedField(field);

        hql.append("   fly_to_ascii(lower(cast(");

        if (!field.contains("."))
            hql.append(alias).append(".");

        hql.append(field.trim())
                .append(" as string))) like fly_to_ascii(cast(:value as string)) \n ");
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

    public Optional<Long> getRecordListCount(Long id, String listName) {
        final String entityName = getEntityName();

        StringBuilder hql = new StringBuilder()
                .append("select count(entities.id)  \nfrom  ").append(entityName).append(" super  \n")
                .append("inner join super.").append(listName).append(" as entities \n")
                .append("where super.id = :id\n");

        final TypedQuery<Long> query = getEntityManager().createQuery(hql.toString(), Long.class);
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

    protected <L> Optional<List<Map<String, L>>> getResultListMap(StringBuilder hql) {
        return getResultListMap(hql, null, 0);
    }

    protected <L> Optional<List<Map<String, L>>> getResultListMap(StringBuilder hql, Map<String, Object> parameters) {
        return getResultListMap(hql, parameters, 0);
    }

    protected <L> Optional<List<Map<String, L>>> getResultListMap(StringBuilder hql, Map<String, Object> parameters, int limit) {
        final Query query = getEntityManager().createQuery(hql.toString(), Map.class);

        if (limit > 0)
            query.setMaxResults(limit);

        if (parameters != null)
            parameters.forEach(query::setParameter);

        List list = query.getResultList();

        if (list == null || list.size() == 0)
            return Optional.empty();

        return Optional.of(list);
    }

    public Optional<List<T>> getResultList(StringBuilder hql) {
        return getResultList(hql, null, 0, getEntityClass());
    }

    public Optional<List<T>> getResultList(StringBuilder hql, Map<String, Object> parameters) {
        return getResultList(hql, parameters, 0, getEntityClass());
    }

    public Optional<List<T>> getResultList(StringBuilder hql, Map<String, Object> parameters, int limit) {
        return getResultList(hql, parameters, limit, getEntityClass());
    }

    public <N> Optional<List<N>> getResultList(StringBuilder hql, Class<N> nClass) {
        return getResultList(hql, null, 0, nClass);
    }

    public <N> Optional<List<N>> getResultList(StringBuilder hql, Map<String, Object> parameters, Class<N> nClass) {
        return getResultList(hql, parameters, 0, nClass);
    }

    public <N> Optional<List<N>> getResultList(StringBuilder hql, Map<String, Object> parameters,
                                               int limit, Class<N> nClass) {
        final TypedQuery<N> query = getEntityManager().createQuery(hql.toString(), nClass);

        if (limit > 0)
            query.setMaxResults(limit);

        if (parameters != null)
            parameters.forEach(query::setParameter);

        final List<N> list = query.getResultList();

        if (isEmpty(list))
            return Optional.empty();

        return Optional.of(list);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void batchSave(List<T> entities, int batchSize) {
        final int entityCount = entities.size();

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

    public <N> Optional<N> getValue(@NonNull StringBuilder hql, @NonNull Long id) {
        return getValue(hql.toString(), id, null);
    }

    public <N> Optional<N> getValue(@NonNull String hql, @NonNull Long id) {
        return getValue(hql, id, null);
    }

    public <N> Optional<N> getValue(@NonNull StringBuilder hql, @NonNull Long id, String tenant) {
        return getValue(hql.toString(), id, tenant);
    }

    public <N> Optional<N> getValue(@NonNull String hql, @NonNull Long id, String tenant) {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", id);

        return getValue(hql, parameters, tenant);
    }

    public <N> Optional<N> getValue(@NonNull String hql, Map<String, Object> parameters) {
        return getValue(hql, parameters, null);
    }

    public <N> Optional<N> getValue(@NonNull StringBuilder hql, Map<String, Object> parameters) {
        return getValue(hql, parameters, null);
    }

    public <N> Optional<N> getValue(@NonNull StringBuilder hql, Map<String, Object> parameters, String tenant) {
        return getValue(hql.toString(), parameters, tenant);
    }

    public <N> Optional<N> getValue(@NonNull String hql, Map<String, Object> parameters, String tenant) {
        final Session session = getNewSession(tenant);

        try {
            Query query = createQuery(hql, session);

            if (parameters != null) {
                parameters.forEach(query::setParameter);
            }

            final Optional<N> result = query.getResultList().stream().filter(Objects::nonNull).findFirst();

            closeSession(session);

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            rollbackSessionTransaction(session);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public void setTenantInCurrentConnection(String tenantIdentifier) {
        flush();

        if (tenantIdentifier != null) {
            tenantIdentifier = "SET search_path TO  " + tenantIdentifier;
        } else {
            tenantIdentifier = "SET search_path TO  " + FlyMultiTenantConstants.DEFAULT_TENANT_ID;
        }

        getEntityManager().createNativeQuery(tenantIdentifier).executeUpdate();

        flush();
    }

    public boolean isInactive(Long id) {
        if (isEmpty(id))
            return false;

        final Optional<Boolean> inative = getFieldById(id, "inactive");

        return inative.orElse(false);
    }

    public boolean hasAnyPermission(String... roles) {
        if (roles == null || roles.length == 0) {
            return false;
        }

        final SecurityContext securityContext = SecurityContextHolder.getContext();
        final Authentication authentication = securityContext.getAuthentication();

        if (authentication != null) {
            List<String> rolesList = Arrays.asList(roles);

            return authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> rolesList.contains(grantedAuthority.getAuthority()));
        }
        return false;
    }

    public Optional<T> find(Long id, String tenant) {
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

    public Optional<List<T>> findAll(String tenant) {
        return findAll(null, null, getEntityClass(), tenant, false);
    }

    public Optional<List<T>> findAll(String columnReference, Object value) {
        return findAll(columnReference, value, getEntityClass(), null);
    }

    public <N> Optional<List<N>> findAll(String columnReference,
                                         Object value, Class<?> nClass) {
        return findAll(columnReference, value, nClass, null);
    }

    public Optional<List<T>> findAll(String columnReference, Object value, String tenant) {
        return findAll(columnReference, value, getEntityClass(), tenant);
    }

    public <N> Optional<List<N>> findAll(String columnReference,
                                         Object value, Class<?> nClass, String tenant) {
        return findAll(columnReference, value, nClass, tenant, true);
    }

    private <N> Optional<List<N>> findAll(String columnReference,
                                          Object value, Class<?> nClass,
                                          String tenant,
                                          boolean isColumnReferenceRequired) {

        if (isColumnReferenceRequired) {
            if (isEmpty(columnReference) || isEmpty(value))
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
            hql.append("where \n ").append(columnReference)
                    .append(" = :value");

            parameter.put("value", value);
        }

        return findAllByInstruction(hql, parameter, nClass, tenant);
    }

    public Optional<T> findByInstruction(@NonNull String hql) {
        return findByInstruction(hql, null, null);
    }


    public Optional<T> findByInstruction(@NonNull StringBuilder hql) {
        return findByInstruction(hql, null, null);
    }

    public Optional<T> findByInstruction(@NonNull String hql, String tenant) {
        return findByInstruction(hql, null, tenant);
    }


    public Optional<T> findByInstruction(@NonNull StringBuilder hql, String tenant) {
        return findByInstruction(hql, null, tenant);
    }

    public Optional<T> findByInstruction(@NonNull StringBuilder hql,
                                         Map<String, ?> parameters,
                                         String tenant) {
        return findByInstruction(hql.toString(), parameters, tenant);
    }

    public <N> Optional<N> findByInstruction(@NonNull StringBuilder hql,
                                             Map<String, ?> parameters,
                                             Class<?> nClass,
                                             String tenant) {
        return findByInstruction(hql.toString(), parameters, nClass, tenant);
    }

    public Optional<T> findByInstruction(@NonNull String hql,
                                         Map<String, ?> parameters,
                                         String tenant) {
        return findByInstruction(hql, parameters, getEntityClass(), tenant);
    }

    public <N> Optional<N> findByInstruction(@NonNull String hql,
                                             Map<String, ?> parameters,
                                             Class<?> nClass,
                                             String tenant) {
        final Session session = getNewSession(tenant);

        try {
            TypedQuery<?> query = createTypedQuery(hql, nClass, session);

            if (parameters != null) {
                parameters.forEach(query::setParameter);
            }

            final Optional<?> result = query
                    .setMaxResults(1)
                    .getResultList().stream().filter(Objects::nonNull).findFirst();

            closeSession(session);

            return (Optional<N>) result;
        } catch (Exception e) {
            e.printStackTrace();
            rollbackSessionTransaction(session);
            throw new RuntimeException(e.getMessage());
        }
    }

    public Optional<List<T>> findAllByInstruction(@NonNull StringBuilder hql,
                                                  Map<String, ?> parameters) {
        return findAllByInstruction(hql, parameters, getEntityClass(), null);
    }

    public Optional<List<T>> findAllByInstruction(@NonNull StringBuilder hql,
                                                  Map<String, ?> parameters,
                                                  String tenant) {
        return findAllByInstruction(hql, parameters, getEntityClass(), tenant);
    }

    public Optional<List<T>> findAllByInstruction(@NonNull String hql,
                                                  Map<String, ?> parameters) {
        return findAllByInstruction(hql, parameters, getEntityClass(), null);
    }

    public Optional<List<T>> findAllByInstruction(@NonNull String hql,
                                                  Map<String, ?> parameters,
                                                  String tenant) {
        return findAllByInstruction(hql, parameters, getEntityClass(), tenant);
    }

    public <N> Optional<List<N>> findAllByInstruction(@NonNull StringBuilder hql,
                                                      Map<String, ?> parameters,
                                                      Class<?> nClass,
                                                      String tenant) {
        return findAllByInstruction(hql.toString(), parameters, nClass, tenant);
    }

    public <N> Optional<List<N>> findAllByInstruction(@NonNull String hql,
                                                      Map<String, ?> parameters,
                                                      Class<?> nClass,
                                                      String tenant) {
        final Session session = getNewSession(tenant);

        try {
            final TypedQuery<?> query = createTypedQuery(hql, nClass, session);

            if (parameters != null) {
                parameters.forEach(query::setParameter);
            }

            final List<?> provider = query.getResultList();

            closeSession(session);

            if (isEmpty(provider))
                return Optional.empty();

            return Optional.ofNullable((List<N>) provider);
        } catch (Exception e) {
            e.printStackTrace();
            rollbackSessionTransaction(session);
            throw new RuntimeException(e.getMessage());
        }
    }

    protected TypedQuery<T> createTypedQuery(@NonNull StringBuilder hql, Session session) {
        return createTypedQuery(hql.toString(), session);
    }

    protected TypedQuery<T> createTypedQuery(@NonNull String hql, Session session) {
        return createTypedQuery(hql, getEntityClass(), session);
    }

    protected <N> TypedQuery<N> createTypedQuery(@NonNull String hql, Class<N> nClass, Session session) {
        if (session != null)
            return session.createQuery(hql, nClass);

        return getEntityManager().createQuery(hql, nClass);
    }

    protected Query createQuery(@NonNull StringBuilder hql, Session session) {
        return createTypedQuery(hql.toString(), session);
    }

    protected Query createQuery(@NonNull String hql, Session session) {
        if (session != null)
            return session.createQuery(hql);

        return getEntityManager().createQuery(hql);
    }

    public void rollbackSessionTransaction(Session session) {
        if (session != null)
            session.getTransaction().rollback();
    }

    public void closeSession(Session session) {
        if (session != null)
            session.close();
    }

    @Transactional
    public Session getNewSession(String tenant) {
        if (isEmpty(tenant))
            return null;

        EntityManager entityManager = getEntityManagerFactory().createEntityManager();

        final SessionFactory sessionFactory = entityManager
                .unwrap(Session.class)
                .getSessionFactory();

        return sessionFactory
                .withOptions()
                .tenantIdentifier(tenant)
                .openSession();
    }

    public void delete(T entity, String tenant) {
        if (isEmpty(tenant) || isEmpty(entity))
            return;

        final Session session = getNewSession(tenant);

        try {
            session.delete(entity);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> getMapParameter(String key, Object value) {
        Map<String, Object> parameter = new HashMap<>();
        parameter.put(key, value);
        return parameter;
    }
}