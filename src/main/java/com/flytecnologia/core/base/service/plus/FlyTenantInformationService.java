package com.flytecnologia.core.base.service.plus;

import com.flytecnologia.core.exception.BE;
import com.flytecnologia.core.exception.BusinessException;
import com.flytecnologia.core.hibernate.multitenancy.FlyMultiTenantConstants;
import com.flytecnologia.core.hibernate.multitenancy.FlyTenantThreadLocal;
import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.model.FlyEntityManualIdImpl;
import com.flytecnologia.core.search.FlyFilter;
import com.flytecnologia.core.search.FlyPageableResult;
import com.flytecnologia.core.spring.FlyValidatorUtil;
import com.flytecnologia.core.token.FlyTokenUserDetails;
import com.flytecnologia.core.util.FlyReflection;
import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.beans.BeanUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.flytecnologia.core.base.service.plus.FlyCloneEntityService.cloneEntity;
import static com.flytecnologia.core.base.service.plus.FlyInvokeBaseLazyAtributesService.invokeBaseLazyAtributesService;
import static com.flytecnologia.core.base.service.plus.FlyValidateEmptyService.isEmpty;
import static com.flytecnologia.core.base.service.plus.FlyValidateEmptyService.isNotEmpty;
import static com.flytecnologia.core.base.service.plus.FlyValidateEmptyService.notNull;

public class FlyTenantInformationService {

    public static String getTenant() {
        String tenantId = FlyTenantThreadLocal.getTenant();

        if (isEmpty(tenantId)) {
            String currentSchemaName = FlyTokenUserDetails.getCurrentSchemaName();

            if (isNotEmpty(currentSchemaName)) {
                tenantId = FlyMultiTenantConstants.DEFAULT_TENANT_SUFFIX + currentSchemaName;
            } else {
                tenantId = FlyMultiTenantConstants.DEFAULT_TENANT_ID;
            }
        }

        return tenantId;
    }

    public static Long getUserId() {
        Long userId = FlyTenantThreadLocal.getUserId();

        if (isEmpty(userId))
            userId = FlyTokenUserDetails.getCurrentUserId();

        return userId;
    }

    @AllArgsConstructor
    public static class FlyMessageService {
        private MessageSource messageSource;

        public String getMessage(String field) {
            return messageSource.getMessage(field, null, LocaleContextHolder.getLocale());
        }
    }

    public static interface FlyRecordCountService<T extends FlyEntity, F extends FlyFilter> extends
            FlyGetRepositoryService<T, F> {
        default Optional<Long> getRecordListCount(Long id, String listName) {
            return getRepository().getRecordListCount(id, listName);
        }
    }

    public static interface FlySaveService<T extends FlyEntity, F extends FlyFilter>
            extends FlyPrintService<F> , FlyFindService<T, F> {

        default void beforeSave(final T entity, final T oldEntity) {
        }

        default void afterSave(final T entity, final T oldEntity) {
        }

        default void beforeValidateSave(T entity) {
        }

        @Transactional
        default T save(T entity, String tenant) {
            entity.setDestinationTenant(tenant);

            return save(entity);
        }

        @Transactional
        default T save(T entity) {
            if (entity.getId() == null) {
                return create(entity);
            }

            return update(entity.getId(), entity);
        }

        default int removeEmptyEntityFromEntityByLevel() {
            return 0;
        }

        default int removeEmptyEntityFromEntityToLevel() {
            return 2;
        }

        default void removeEmptyEntityFromEntity(T entity) {
            FlyReflection.removeEmptyEntityFromEntity(
                    entity,
                    removeEmptyEntityFromEntityByLevel(),
                    removeEmptyEntityFromEntityToLevel()
            );
        }

        default void setParentInTheChildrenList(T entity) {
            FlyReflection.setParentInTheChildrenList(entity);
        }

        default void validateBeforeCreate(T entity) {
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

        default void validateIdInUse(T entity) {
            if (!(entity instanceof FlyEntityManualIdImpl))
                return;

            if (getRepository().existsById(entity.getId())) {
                throw new BE("flyserivice.idInUse");
            }
        }

        @Transactional
        default T create(T entity, String tenant) {
            entity.setDestinationTenant(tenant);
            return create(entity);
        }

        @Transactional
        default T create(T entity) {
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

        default T saveOrUpdate(T entity) {
            final Session session = getRepository().getNewSession(entity.getDestinationTenant());

            if (session == null) {
                return getRepository().save(entity);
            } else try {
                Transaction tx = session.beginTransaction();

                Long id;

                if (entity instanceof HibernateProxy && entity.getId() != null) {
                    session.update(entity);
                    id = entity.getId();
                } else {
                    id = (Long) session.save(entity);
                }

                entity = (T) session.find(entity.getClass(), id);

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

        @Transactional
        default T update(Long id, T entity, String tenant) {
            entity.setDestinationTenant(tenant);
            return update(id, entity);
        }

        @Transactional
        default T update(Long id, T entity) {
            notNull(id, "flyserivice.idNotNull");
            notNull(entity, "flyserivice.invalidRecord");
            notNull(entity.getId(), "flyserivice.invalidRecord");

            if (!entity.isIgnoreBeforeSave()) {
                beforeValidateSave(entity);
            }

            FlyValidatorUtil.validate(entity);

            final Optional<T> entitySavedOptional = find(id);

            final String entityName = entity.getClass().getSimpleName();

            T entitySaved = entitySavedOptional
                    .orElseThrow(() -> new EmptyResultDataAccessException("update " + entityName + " -> " + id, 1));

            invokeBaseLazyAtributesService(entitySaved);

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

        default void validateDifferentId(Long id, T entity) {
            if (!id.equals(entity.getId())) {
                throw new BusinessException("flyserivice.differentId");
            }
        }

        default void batchSave(List<T> entities) {
            batchSave(entities, 250);
        }

        default void batchSave(List<T> entities, int batchSize) {
            getRepository().batchSave(entities, batchSize);
        }

        default void batchSaveComplete(List<T> entities) {
            batchSaveComplete(entities, 250);
        }

        default void batchSaveComplete(List<T> entities, int batchSize) {
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

    }

    public static interface FlySearchService<T extends FlyEntity, F extends FlyFilter>
            extends FlyGetRepositoryService<T, F> {
        default FlyPageableResult search(F filter, Pageable pageable) {
            return getRepository().search(filter, pageable);
        }
    }

    public static interface FlyTenantService<T extends FlyEntity, F extends FlyFilter> extends
            FlyGetRepositoryService<T, F> {
        default void setTenantInCurrentSession(String tenant) {
            if (isEmpty(tenant))
                return;

            FlyTenantThreadLocal.setTenant(tenant);

            getRepository().setTenantInCurrentConnection(tenant);
        }
    }
}
