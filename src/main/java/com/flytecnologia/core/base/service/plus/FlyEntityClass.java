package com.flytecnologia.core.base.service.plus;

import com.flytecnologia.core.model.FlyEntity;

import java.lang.reflect.ParameterizedType;

public interface FlyEntityClass<T extends FlyEntity> {
    default Class<T> getEntityClass() {
        return (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    default String getEntityName() {
        return getEntityClass().getSimpleName();
    }

    default String getAlias() {
        String entityName = getEntityName();

        return entityName.substring(0, 1).toLowerCase() + entityName.substring(1);
    }
}
