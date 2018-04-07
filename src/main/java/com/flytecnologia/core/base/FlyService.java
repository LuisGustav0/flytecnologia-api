package com.flytecnologia.core.base;

import com.flytecnologia.core.exception.BusinessException;
import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.model.FlyEntityWithInactive;
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

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class FlyService<T extends FlyEntity, F extends FlyFilter> {

    protected abstract FlyRepository<T, Long, F> getRepository();

    public Optional<T> findById(Long id) {
        //return getRepository().findById(id).orElse(null);
        return getRepository().findById(id);
    }

    protected void beforeSave(T entity, T oldEntity) {
    }

    protected void afterSave(T entity, T oldEntity) {
    }

    protected void beforeDelete(T entity) {
    }

    protected void afterDelete(Long id) {
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

        addDefaultValuesBeforeCreate(entity);

        entity = getRepository().save(entity);

        entity.setParameters(parameters);

        if (!isIgnoreAfterSave) {
            afterSave(entity, null);
        }

        entity.setParameters(null);

        return entity;
    }

    private void addDefaultValuesBeforeCreate(T entity) {
        if (entity instanceof FlyEntityWithInactive) {
            if (((FlyEntityWithInactive) entity).getInactive() == null) {
                ((FlyEntityWithInactive) entity).setInactive(false);
            }
        }
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


        dispachLazyAtributesToUpdate(entitySaved);

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
        BeanUtils.copyProperties(entity, entitySaved, "id", "gruposPermissaoUsuario");

        Map<String, Object> parameters = entity.getParameters();

        T _entitySaved = getRepository().save(entitySaved);

        _entitySaved.setParameters(parameters);

        if (!isIgnoreAfterSave) {
            afterSave(_entitySaved, entity);
        }

        _entitySaved.setParameters(null);

        return _entitySaved;
    }

    @Transactional
    protected void dispachLazyAtributesToUpdate(T entitySaved) {
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

    public Optional<List> getItensAutocomplete(F filter) {
        beforeSearchAutoComplete(filter);

        return getRepository().getItensAutocomplete(filter);
    }

    public Optional<Map> getItemAutocomplete(F filter) {
        beforeSearchAutoComplete(filter);

        return getRepository().getItemAutocomplete(filter);
    }

    protected void beforeSearchAutoComplete(F filter) {
    }

    protected boolean isNotEmpty(Object value) {
        return !isEmpty(value);
    }

    protected boolean isEmpty(Object value) {
        return getRepository().isEmpty(value);
    }

    protected boolean isTrue(Object value) {
        return isTrue((Boolean) value);
    }

    protected boolean isTrue(Boolean value) {
        return value != null && value;
    }

    protected boolean isFalse(Object value) {
        return isFalse((Boolean) value);
    }

    protected boolean isFalse(Boolean value) {
        return value != null && !value;
    }

    protected void notNull(Object object, String message) {
        if (object == null) {
            throw new BusinessException(message);
        }
    }

    protected void notEmpty(Object object, String message) {
        if (isEmpty(object)) {
            throw new BusinessException(message);
        }
    }

    public Map<String, Object> defaultValues() {
        Map<String, Object> mapOfValues = new HashMap<>();
        addDefaultValues(mapOfValues);
        return mapOfValues;
    }

    public void addDefaultValues(Map<String, Object> mapOfValues) {

    }

    public Map<String, Object> defaultValuesSearch() {
        Map<String, Object> mapOfValues = new HashMap<>();
        addDefaultValuesSearch(mapOfValues);
        return mapOfValues;
    }

    public void addDefaultValuesSearch(Map<String, Object> mapOfValues) {

    }

    public FlyPageableResult search(F filter, Pageable pageable) {
        return getRepository().search(filter, pageable);
    }

    protected void validateDateLessOrEquals(LocalDate firstDate, LocalDate lastDate, String message) {
        if (firstDate == null || lastDate == null)
            return;

        if (lastDate.isBefore(firstDate))
            throw new BusinessException(message);
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
}
