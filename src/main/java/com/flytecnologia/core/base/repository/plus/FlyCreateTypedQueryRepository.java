package com.flytecnologia.core.base.repository.plus;

import com.flytecnologia.core.base.service.plus.FlyEntityClass;
import com.flytecnologia.core.model.FlyEntity;
import lombok.NonNull;
import org.hibernate.Session;

import javax.persistence.TypedQuery;

public interface FlyCreateTypedQueryRepository<T extends FlyEntity> extends
        FlyEntityManagerRepository,
        FlyEntityClass<T> {

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
}
