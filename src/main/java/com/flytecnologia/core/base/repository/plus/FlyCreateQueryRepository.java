package com.flytecnologia.core.base.repository.plus;

import com.flytecnologia.core.model.FlyEntity;
import lombok.NonNull;
import org.hibernate.Session;

import javax.persistence.Query;

public interface FlyCreateQueryRepository<T extends FlyEntity> extends
        FlyEntityManagerRepository,
        FlyCreateTypedQueryRepository<T> {

    default Query createQuery(@NonNull StringBuilder hql, Session session) {
        return createTypedQuery(hql.toString(), session);
    }

    default Query createQuery(@NonNull String hql, Session session) {
        if (session != null)
            return session.createQuery(hql);

        return getEntityManager().createQuery(hql);
    }
}
