package com.flytecnologia.core.hibernate.envers;

import com.flytecnologia.core.model.FlyEntityImpl;
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
        if(event.getEntity() instanceof FlyEntityImpl) {
            boolean doNotAudit = ((FlyEntityImpl) event.getEntity()).isDoNotAudit();

            if (!doNotAudit) {
                super.onPostInsert(event);
            }
        } else {
            super.onPostInsert(event);
        }
    }
}
