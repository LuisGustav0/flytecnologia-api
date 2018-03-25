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
        boolean doNotAudit = ((FlyEntity) event.getEntity()).isDoNotAudit();

        if (!doNotAudit) {
            super.onPostInsert(event);
        }
    }
}