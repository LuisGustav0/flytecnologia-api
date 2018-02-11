package com.flytecnologia.core.base;

import com.flytecnologia.core.exception.BusinessException;
import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyAutoCompleteFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class FlyService<T extends FlyEntity> {

    protected abstract FlyRepository<T, Long> getRepository();

    public T findById(Long id) {
        return getRepository().findOne(id);
    }

    protected void beforeSave(T entity, T oldEntity) {
    }

    protected void afterSave(T entity) {
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
        notNull(entity, "flyserivice.invalidRecord");

        beforeSave(entity, null);

        entity = getRepository().save(entity);

        afterSave(entity);

        return entity;
    }

    protected String getEntityName() {
        return getRepository().getEntityName();
    }

    protected void treatEntity(T entity, T entitySaved) {

    }

    @Transactional
    public T update(Long id, T entity) {
        notNull(id, "flyserivice.idNotNull");
        notNull(entity, "flyserivice.invalidRecord");
        notNull(entity.getId(), "flyserivice.invalidRecord");

        T entitySaved = getRepository().findOne(id);

        if (entitySaved == null) {
            throw new EmptyResultDataAccessException("update " + getEntityName() + " -> " + id, 1);
        }

        if (!id.equals(entity.getId())) {
            throw new BusinessException("flyserivice.differentId");
        }

        /*if (entity.getVersion() != null && !entity.getVersion().equals(entitySaved.getVersion())) {
            throw new BusinessException("flyserivice.differentVersion");
        }*/

        beforeSave(entity, entitySaved);

        treatEntity(entity, entitySaved);

        /*Para fazer update todos os versions dos objetos aninhados tem q estar setados*/
        BeanUtils.copyProperties(entity, entitySaved, "id", "gruposPermissaoUsuario");

        entitySaved = getRepository().save(entitySaved);

        afterSave(entitySaved);

        return entitySaved;
    }

    @Transactional
    public void delete(Long id) {
        notNull(id, "flyserivice.idNotNull");

        T entity = getRepository().findOne(id);

        if (entity == null) {
            throw new EmptyResultDataAccessException("delete " + getEntityName() + " -> " + id, 1);
        }

        beforeDelete(entity);

        getRepository().delete(entity);

        afterDelete(id);
    }

    public List<Map<String, Object>> getListAutocomplete(FlyAutoCompleteFilter acFilter, Map<String, Object> params) {
        beforeSearchAutoComplete(acFilter, params);

        return getRepository().getItensAutocomplete(acFilter, params);
    }

    public Map<String, Object> getItemAutocomplete(FlyAutoCompleteFilter acFilter, Map<String, Object> params) {
        beforeSearchAutoComplete(acFilter, params);

        return getRepository().getItemAutocomplete(acFilter, params);
    }

    protected void beforeSearchAutoComplete(FlyAutoCompleteFilter acFilter, Map<String, Object> params) {
    }

    protected boolean isNotEmpty(Object value) {
        return !isEmpty(value);
    }

    protected boolean isEmpty(Object value) {
        return getRepository().isEmpty(value);
    }

    protected boolean isEmptyList(Collection<?> list) {
        return list == null || list.isEmpty();
    }

    protected boolean isTrue(Boolean value) {
        return value != null && value;
    }

    protected boolean isFalse(Boolean value) {
        return value != null && !value;
    }

    protected void notNull(Object object, String message) {
        if (object == null) {
            throw new BusinessException(message);
        }
    }

    public Map<String, Object> defaultValuesCrud() {
        Map<String, Object> mapOfValues = new HashMap<>();
        addDefaultValuesCrud(mapOfValues);
        return mapOfValues;
    }

    public void addDefaultValuesCrud(Map<String, Object> mapOfValues) {

    }

    public Map<String, Object> defaultValuesSearch() {
        Map<String, Object> mapOfValues = new HashMap<>();
        addDefaultValuesCrud(mapOfValues);
        return mapOfValues;
    }

    public void addDefaultValuesSearch(Map<String, Object> mapOfValues) {

    }
}
