package com.flytecnologia.core.base;

import com.flytecnologia.core.model.FlyEntity;

import javax.persistence.EntityManager;

public interface FlyRepository<T extends FlyEntity> {
    EntityManager getEntityManager();
    Class<T> getEntityClass();
}
