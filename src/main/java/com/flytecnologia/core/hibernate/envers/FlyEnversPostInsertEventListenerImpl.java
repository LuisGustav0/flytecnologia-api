package com.flytecnologia.core.hibernate.envers;

import com.flytecnologia.core.model.FlyEntity;
import org.hibernate.envers.boot.internal.EnversService;
import org.hibernate.envers.event.spi.EnversPostInsertEventListenerImpl;
import org.hibernate.event.spi.PostInsertEvent;

public class FlyEnversPostInsertEventListenerImpl extends
        EnversPostInsertEventListenerImpl {

    private static final long serialVersionUID = 1L;

    public FlyEnversPostInsertEventListenerImpl(EnversService enversService) {
        super(enversService);
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        if (event.getEntity() instanceof FlyEntity) {
            boolean doNotAudit = ((FlyEntity) event.getEntity()).isIgnoreAudit();

            if (!doNotAudit) {
                super.onPostInsert(event);
            }
        } else {
            super.onPostInsert(event);
        }
    }
}
