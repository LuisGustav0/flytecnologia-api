package com.flytecnologia.core.hibernate.envers;

import org.hibernate.HibernateException;
import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.envers.boot.internal.EnversService;
import org.hibernate.envers.event.spi.*;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

public class FlyEnversIntegrator implements Integrator { //see EnversIntegrator
    @Override
    public void integrate(Metadata metadata,
                          SessionFactoryImplementor sessionFactory,
                          SessionFactoryServiceRegistry serviceRegistry) {
        final EnversService enversService = serviceRegistry.getService(EnversService.class);

        if (!enversService.isEnabled()) {
            return;
        }

        if (!enversService.isInitialized()) {
            throw new HibernateException("Expecting EnversService to have been initialized prior to call to EnversIntegrator#integrate");
        }

        final EventListenerRegistry listenerRegistry = serviceRegistry.getService(EventListenerRegistry.class);
        listenerRegistry.addDuplicationStrategy(EnversListenerDuplicationStrategy.INSTANCE);

        if (enversService.getEntitiesConfigurations().hasAuditedEntities()) {
            listenerRegistry.appendListeners(
                    EventType.PRE_UPDATE,
                    new EnversPreUpdateEventListenerImpl(enversService)
            );
            listenerRegistry.appendListeners(
                    EventType.PRE_COLLECTION_REMOVE,
                    new EnversPreCollectionRemoveEventListenerImpl(enversService)
            );
            listenerRegistry.appendListeners(
                    EventType.PRE_COLLECTION_UPDATE,
                    new EnversPreCollectionUpdateEventListenerImpl(enversService)
            );
            listenerRegistry.appendListeners(
                    EventType.POST_UPDATE,
                    new FlyEnversPostUpdateEventListenerImpl(enversService)
            );
            listenerRegistry.appendListeners(
                    EventType.POST_DELETE,
                    new FlyEnversPostDeleteEventListenerImpl(enversService)
            );
            listenerRegistry.appendListeners(
                    EventType.POST_INSERT,
                    new FlyEnversPostInsertEventListenerImpl(enversService)
            );
            listenerRegistry.appendListeners(
                    EventType.POST_COLLECTION_RECREATE,
                    new EnversPostCollectionRecreateEventListenerImpl(enversService)
            );
        }
    }

    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactory,
                             SessionFactoryServiceRegistry serviceRegistry) {
        /*obligate to implement*/
    }
}
