package com.flytecnologia.core.base;

import com.flytecnologia.core.exception.BusinessException;
import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.model.FlyEntityImpl;
import com.flytecnologia.core.search.FlyFilter;
import com.flytecnologia.core.search.FlyPageableResult;
import com.flytecnologia.core.user.FlyUserDetailsService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Basic;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class FlyService<T extends FlyEntity, F extends FlyFilter> implements FlyValidationBase {

    protected abstract FlyRepository<T, Long, F> getRepository();

    public Optional<T> findById(Long id) {
        return getRepository().findById(id);
    }

    protected void beforeSave(final T entity, final T oldEntity) {
    }

    protected void afterSave(final T entity, final T oldEntity) {
    }

    protected void beforeDelete(final T entity) {
    }

    protected void afterDelete(Long id) {
    }

    protected void beforeDeleteAll(List<T> entities) {
    }

    protected void afterDeleteAll(List<T> entities) {
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
    public T save(T entity) {
        if (entity.getId() == null) {
            return create(entity);
        }

        return update(entity.getId(), entity);
    }

    @Transactional
    public T create(T entity) {
        notNull(entity, "flyserivice.invalidRecord");

        if (!entity.isIgnoreBeforeSave()) {
            beforeSave(entity, null);
        }

        boolean isIgnoreAfterSave = entity.isIgnoreAfterSave();

        Map<String, Object> parameters = entity.getParameters();

        entity = getRepository().save(entity);

        entity.setParameters(parameters);

        if (!isIgnoreAfterSave) {
            afterSave(entity, null);
        }

        entity.setParameters(null);

        return entity;
    }

    protected String getEntityName() {
        return getRepository().getEntityName();
    }

    @Transactional
    public T update(Long id, T entity) {
        notNull(id, "flyserivice.idNotNull");
        notNull(entity, "flyserivice.invalidRecord");
        notNull(entity.getId(), "flyserivice.invalidRecord");

        Optional<T> entitySavedOptional = findById(id);

        T entitySaved = entitySavedOptional.orElseThrow(() -> new EmptyResultDataAccessException("update " + getEntityName() + " -> " + id, 1));

        invokeBaseLazyAtributesToUpdate(entitySaved);

        if (!id.equals(entity.getId())) {
            throw new BusinessException("flyserivice.differentId");
        }

        /*if (entity.getVersion() != null && !entity.getVersion().equals(entitySaved.getVersion())) {
            throw new BusinessException("flyserivice.differentVersion");
        }*/

        if (!entity.isIgnoreBeforeSave()) {
            beforeSave(entity, entitySaved);
        }

        boolean isIgnoreAfterSave = entity.isIgnoreAfterSave();

        /*Para fazer update todos os versions dos objetos aninhados tem q estar setados*/
        BeanUtils.copyProperties(entity, entitySaved, "id");

        Map<String, Object> parameters = entity.getParameters();

        T _entitySaved = getRepository().save(entitySaved);

        _entitySaved.setParameters(parameters);

        if (!isIgnoreAfterSave) {
            afterSave(_entitySaved, entity);
        }

        _entitySaved.setParameters(null);

        return _entitySaved;
    }

    private void invokeBaseLazyAtributesToUpdate(T entitySaved) {
        Field[] fields = getEntityClass().getDeclaredFields();

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
        delete(id, false, false);
    }

    @Transactional
    public void delete(Long id, boolean isIgnoreBeforeDelete, boolean isIgnoreAfterDelete) {
        notNull(id, "flyserivice.idNotNull");

        Optional<T> entityOptional = findById(id);

        T entity = entityOptional.orElseThrow(() -> new EmptyResultDataAccessException("delete " + getEntityName() + " -> " + id, 1));

        if (!isIgnoreBeforeDelete) {
            beforeDelete(entity);
        }

        getRepository().delete(entity);

        if (!isIgnoreAfterDelete) {
            afterDelete(id);
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

    public Optional<List> getItemsAutocomplete(F filter) {
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

    public byte[] getReport(F filter) {
        return null;
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

    public Long getUserId() {
        return FlyUserDetailsService.getCurrentUserId();
    }

    public Map<String, String> findImageById(Long id, String field) {
        return getRepository().findImageById(id, field);
    }

    public String removeBase64Information(String encode) {
        if (isEmpty(encode))
            return encode;

        int indexOf = encode.indexOf(";base64,");

        if (indexOf <= 0)
            return encode;

        return encode.substring(indexOf + 8);
    }

    public T getReference(Long id) {
        return getRepository().getReference(id);
    }

    public String convertToBase64(byte[] data) {
        if (data == null)
            return null;

        return new String(Base64.getEncoder().encode(data));
    }

    public void detach(FlyEntityImpl entity) {
        getRepository().detach(entity);
    }
}
