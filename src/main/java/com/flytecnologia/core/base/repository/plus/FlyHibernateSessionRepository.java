package com.flytecnologia.core.base.repository.plus;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public interface FlyHibernateSessionRepository {
    EntityManagerFactory getEntityManagerFactory();

    default void rollbackSessionTransaction(Session session) {
        if (session != null)
            session.getTransaction().rollback();
    }

    default void closeSession(Session session) {
        if (session != null)
            session.close();
    }

    @Transactional
    default Session getNewSession(String tenant) {
        if (tenant == null || tenant.trim().length() == 0)
            return null;

        EntityManager entityManager = getEntityManagerFactory().createEntityManager();

        final SessionFactory sessionFactory = entityManager
                .unwrap(Session.class)
                .getSessionFactory();

        return sessionFactory
                .withOptions()
                .tenantIdentifier(tenant)
                .openSession();
    }
}
