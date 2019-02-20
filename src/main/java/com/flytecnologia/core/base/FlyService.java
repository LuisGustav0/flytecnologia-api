package com.flytecnologia.core.base;

import com.flytecnologia.core.base.plusService.FlyPrintService;
import com.flytecnologia.core.base.plusService.FlyTenantInformation;
import com.flytecnologia.core.base.plusService.FlyTimeSpentService;
import com.flytecnologia.core.base.plusService.FlyValidationBase;
import com.flytecnologia.core.exception.BE;
import com.flytecnologia.core.exception.BusinessException;
import com.flytecnologia.core.hibernate.multitenancy.FlyTenantThreadLocal;
import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.model.FlyEntityManualIdImpl;
import com.flytecnologia.core.search.FlyFilter;
import com.flytecnologia.core.search.FlyPageableResult;
import com.flytecnologia.core.spring.FlyValidatorUtil;
import com.flytecnologia.core.util.FlyReflection;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Basic;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class FlyService<T extends FlyEntity, F extends FlyFilter> implements FlyValidationBase,
        FlyTenantInformation, FlyTimeSpentService, FlyPrintService<F> {

    protected abstract FlyRepository<T, Long, F> getRepository();

    public Optional<T> find(Long id) {
        return getRepository().find(id);
    }

    public Optional<T> find(Long id, String tenant) {
        return getRepository().find(id, tenant);
    }

    protected void beforeValidateSave(T entity) {
    }

    protected void beforeSave(final T entity, final T oldEntity) {
    }

    protected void afterSave(final T entity, final T oldEntity) {
    }

    protected void beforeDelete(final T entity) {
    }

    protected void afterDelete(Long id, String tenant) {
    }

    protected void beforeDeleteAll(List<T> entities) {
    }

    protected void afterDeleteAll(List<T> entities) {
    }

    public void flush() {
        getRepository().flush();
    }

    @Autowired
    private MessageSource messageSource;

    public FlyService() {
    }

    public String getMessage(String field) {
        return messageSource.getMessage(field, null, LocaleContextHolder.getLocale());
    }

    protected Class<T> getEntityClass() {
        return getRepository().getEntityClass();
    }


    @Transactional
    public T save(T entity, String tenant) {
        entity.setDestinationTenant(tenant);

        return save(entity);
    }

    @Transactional
    public T save(T entity) {
        if (entity.getId() == null) {
            return create(entity);
        }

        return update(entity.getId(), entity);
    }

    public int removeEmptyEntityFromEntityByLevel() {
        return 0;
    }

    public int removeEmptyEntityFromEntityToLevel() {
        return 2;
    }

    public void removeEmptyEntityFromEntity(T entity) {
        FlyReflection.removeEmptyEntityFromEntity(entity, removeEmptyEntityFromEntityByLevel(), removeEmptyEntityFromEntityToLevel());
    }

    public void setParentInTheChildrenList(T entity) {
        FlyReflection.setParentInTheChildrenList(entity);
    }

    private void validateBeforeCreate(T entity) {
        if (!entity.isIgnoreBeforeSave()) {
            beforeValidateSave(entity);
        }

        FlyValidatorUtil.validate(entity);

        removeEmptyEntityFromEntity(entity);
        setParentInTheChildrenList(entity);

        if (!entity.isIgnoreBeforeSave()) {
            beforeSave(entity, null);
        }

        validateIdInUse(entity);
    }

    protected void validateIdInUse(T entity) {
        if (!(entity instanceof FlyEntityManualIdImpl))
            return;

        if (existsById(entity.getId())) {
            throw new BE("flyserivice.idInUse");
        }
    }

    @Transactional
    public T create(T entity, String tenant) {
        entity.setDestinationTenant(tenant);
        return create(entity);
    }

    @Transactional
    public T create(T entity) {
        notNull(entity, "flyserivice.invalidRecord");

        validateBeforeCreate(entity);

        final boolean isIgnoreAfterSave = entity.isIgnoreAfterSave();

        final Map<String, Object> parameters = entity.getParameters();

        entity = saveOrUpdate(entity);

        entity.setParameters(parameters);

        if (!isIgnoreAfterSave) {
            afterSave(entity, null);
        }

        entity.setParameters(null);

        return entity;
    }

    private T saveOrUpdate(T entity) {
        final Session session = getRepository().getNewSession(entity.getDestinationTenant());

        if (session == null) {
            return getRepository().save(entity);
        } else {
            try {
                Transaction tx = session.beginTransaction();

                Long id;

                if (entity instanceof HibernateProxy && entity.getId() != null) {
                    session.update(entity);
                    id = entity.getId();
                } else {
                    id = (Long) session.save(entity);
                }

                entity = session.find(getEntityClass(), id);

                tx.commit();
                session.close();

                return entity;
            } catch (DataIntegrityViolationException de) {
                de.printStackTrace();
                getRepository().rollbackSessionTransaction(session);
                throw new DataIntegrityViolationException(de.getMessage(), de.getCause());
            } catch (Exception e) {
                e.printStackTrace();
                getRepository().rollbackSessionTransaction(session);
                throw new RuntimeException(e.getMessage(), e.getCause());
            }
        }
    }

    protected String getEntityName() {
        return getRepository().getEntityName();
    }


    @Transactional
    public T update(Long id, T entity, String tenant) {
        entity.setDestinationTenant(tenant);
        return update(id, entity);
    }

    @Transactional
    public T update(Long id, T entity) {
        notNull(id, "flyserivice.idNotNull");
        notNull(entity, "flyserivice.invalidRecord");
        notNull(entity.getId(), "flyserivice.invalidRecord");

        if (!entity.isIgnoreBeforeSave()) {
            beforeValidateSave(entity);
        }

        FlyValidatorUtil.validate(entity);

        final Optional<T> entitySavedOptional = find(id);

        T entitySaved = entitySavedOptional.orElseThrow(() -> new EmptyResultDataAccessException("update " + getEntityName() + " -> " + id, 1));

        invokeBaseLazyAtributesToUpdate(entitySaved);

        validateDifferentId(id, entity);
        //validateDifferentVersion(entity, entitySaved);

        if (!entity.isIgnoreBeforeSave()) {
            beforeSave(entity, entitySaved);
        }

        boolean isIgnoreAfterSave = entity.isIgnoreAfterSave();

        final T oldEntity = cloneEntity(entitySaved);

        /*Para fazer update todos os versions dos objetos aninhados tem q estar setados*/
        BeanUtils.copyProperties(entity, entitySaved, "id");

        final Map<String, Object> parameters = entity.getParameters();

        entitySaved = saveOrUpdate(entitySaved);

        entitySaved.setParameters(parameters);

        if (!isIgnoreAfterSave) {
            afterSave(entitySaved, oldEntity);
        }

        entitySaved.setParameters(null);

        return entitySaved;
    }

    private void validateDifferentId(Long id, T entity) {
        if (!id.equals(entity.getId())) {
            throw new BusinessException("flyserivice.differentId");
        }
    }

   /* private void validateDifferentVersion(T entity, T entitySaved) {
        if (entity.getVersion() != null && !entity.getVersion().equals(entitySaved.getVersion())) {
            throw new BusinessException("flyserivice.differentVersion");
        }
    }*/

    private T cloneEntity(T entitySaved) {
        T oldEntity = null;
        try {
            oldEntity = getEntityClass().newInstance();
            BeanUtils.copyProperties(entitySaved, oldEntity);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return oldEntity;
    }

    private void invokeBaseLazyAtributesToUpdate(T entitySaved) {
        final Field[] fields = getEntityClass().getDeclaredFields();

        for (Field field : fields) {
            Annotation[] annotations = field.getDeclaredAnnotations();

            if (annotations != null && annotations.length > 0) {
                String name = field.getName();

                for (Annotation annotation : annotations) {
                    if (annotation instanceof Basic) {
                        try {
                            String methodName = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);

                            Method method = getEntityClass().getDeclaredMethod(methodName);

                            method.invoke(entitySaved);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @Transactional
    public void delete(Long id) {
        delete(id, false, false, null);
    }


    @Transactional
    public void delete(Long id, String tenant) {
        delete(id, false, false, tenant);
    }

    @Transactional
    public void delete(Long id, boolean isIgnoreBeforeDelete, boolean isIgnoreAfterDelete, String tenant) {
        notNull(id, "flyserivice.idNotNull");

        final Optional<T> entityOptional = find(id, tenant);

        T entity = entityOptional
                .orElseThrow(() -> new EmptyResultDataAccessException("delete " + getEntityName() + " -> " + id, 1));

        if (!isIgnoreBeforeDelete) {
            if (tenant != null) {
                entity.getParameters().put("$tenant", tenant);
            }

            beforeDelete(entity);
        }

        if (tenant != null) {
            getRepository().delete(entity, tenant);
        } else {
            getRepository().delete(entity);
        }

        if (!isIgnoreAfterDelete) {
            afterDelete(id, tenant);
        }
    }


    @Transactional
    public void deleteAll(List<T> entities) {
        deleteAll(entities, false, false);
    }

    @Transactional
    public void deleteAll(List<T> entities, boolean isIgnoreBeforeDelete, boolean isIgnoreAfterDelete) {
        notNull(entities, "flyserivice.listOfEntityNotNull");

        if (!isIgnoreBeforeDelete) {
            beforeDeleteAll(entities);
        }

        getRepository().deleteAll(entities);

        if (!isIgnoreAfterDelete) {
            afterDeleteAll(entities);
        }
    }

    public boolean existsById(Long id) {
        return getRepository().existsById(id);
    }

    public Optional<List<Map<String, Object>>> getItemsAutocomplete(F filter) {
        beforeSearchAutoComplete(filter);

        return getRepository().getItemsAutocomplete(filter);
    }

    public Optional<Map> getItemAutocomplete(F filter) {
        beforeSearchAutoComplete(filter);

        return getRepository().getItemAutocomplete(filter);
    }

    protected void beforeSearchAutoComplete(F filter) {
    }

    public FlyPageableResult search(F filter, Pageable pageable) {
        return getRepository().search(filter, pageable);
    }

    public Optional<Long> goToBefore(F filter) {
        if (filter.getId() == null || filter.getId() == 0) {
            return getFirstId(filter);
        }

        return getPreviousId(filter);
    }

    public Optional<Long> goToAfter(F filter) {
        if (filter.getId() == null || filter.getId() == 0) {
            return getLastId(filter);
        }

        return getNextId(filter);
    }

    public Optional<Long> getFirstId(F filter) {
        return getRepository().getFirstId(filter);
    }

    public Optional<Long> getPreviousId(F filter) {
        return getRepository().getPreviousId(filter);
    }

    public Optional<Long> getLastId(F filter) {
        return getRepository().getLastId(filter);
    }

    public Optional<Long> getNextId(F filter) {
        return getRepository().getNextId(filter);
    }

    public Map<String, String> findImageById(Long id, String field) {
        return getRepository().findImageById(id, field);
    }

    public Optional<T> getReference(Long id) {
        return getRepository().getReference(id);
    }

    public Optional<T> getReference(Long id, String tenant) {
        return getRepository().getReference(id, tenant);
    }

    public <G extends FlyEntity> void detach(G entity) {
        getRepository().detach(entity);
    }

    protected <N> Optional<N> getFieldById(Long id, String property) {
        return getRepository().getFieldById(id, property);
    }

    protected <N> Optional<N> getFieldById(Long id, String property, String tenant) {
        return getRepository().getFieldById(id, property, tenant);
    }

    public boolean isInactive(Long id) {
        return getRepository().isInactive(id);
    }

    protected Optional<Long> getRecordListCount(Long id, String listName) {
        return getRepository().getRecordListCount(id, listName);
    }

    public void batchSave(List<T> entities) {
        batchSave(entities, 250);
    }

    public void batchSave(List<T> entities, int batchSize) {
        getRepository().batchSave(entities, batchSize);
    }

    public void batchSaveComplete(List<T> entities) {
        batchSaveComplete(entities, 250);
    }

    public void batchSaveComplete(List<T> entities, int batchSize) {
        entities.forEach(entity -> {
            validateBeforeCreate(entity);

            final boolean isIgnoreAfterSave = entity.isIgnoreAfterSave();

            if (!isIgnoreAfterSave) {
                afterSave(entity, null);
            }

            entity.setParameters(null);
        });

        getRepository().batchSave(entities, batchSize);
    }

    public boolean hasAnyPermission(String... roles) {
        return getRepository().hasAnyPermission(roles);
    }

    public void setTenantInCurrentSession(String tenant) {
        if (isEmpty(tenant))
            return;

        FlyTenantThreadLocal.setTenant(tenant);

        getRepository().setTenantInCurrentConnection(tenant);
    }

    public Optional<List<T>> findAll(String tenant) {
        return getRepository().findAll(tenant);
    }

    public Optional<List<T>> findAll(String columnReference, Object value, String tenant) {
        return getRepository().findAll(columnReference, value, tenant);
    }

    public Optional<List<T>> findAll(String columnReference, Object value) {
        return getRepository().findAll(columnReference, value);
    }

    public <N> Optional<List<N>> findAll(String columnReference,
                                         Object value, Class<?> nClass, String tenant) {
        return getRepository().findAll(columnReference, value, nClass, tenant);
    }

    public <N> Optional<List<N>> findAll(String columnReference,
                                         Object value, Class<?> nClass) {
        return getRepository().findAll(columnReference, value, nClass);
    }
}