package com.flytecnologia.core.base.repository.plus;

import com.flytecnologia.core.model.FlyEntity;
import lombok.NonNull;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

public interface FlyCreateQueryRepository<T extends FlyEntity> {
    EntityManager getEntityManager();
    Class<T> getEntityClass();

    default TypedQuery<T> createTypedQuery(@NonNull StringBuilder hql, Session session) {
        return createTypedQuery(hql.toString(), session);
    }

    default TypedQuery<T> createTypedQuery(@NonNull String hql, Session session) {
        return createTypedQuery(hql, getEntityClass(), session);
    }

    default <N> TypedQuery<N> createTypedQuery(@NonNull String hql, Class<N> nClass, Session session) {
        if (session != null)
            return session.createQuery(hql, nClass);

        return getEntityManager().createQuery(hql, nClass);
    }

    default Query createQuery(@NonNull StringBuilder hql, Session session) {
        return createTypedQuery(hql.toString(), session);
    }

    default Query createQuery(@NonNull String hql, Session session) {
        if (session != null)
            return session.createQuery(hql);

        return getEntityManager().createQuery(hql);
    }
}
