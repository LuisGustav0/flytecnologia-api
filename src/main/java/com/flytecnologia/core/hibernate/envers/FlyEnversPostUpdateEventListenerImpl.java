package com.flytecnologia.core.hibernate.envers;

import com.flytecnologia.core.model.FlyEntity;
import org.hibernate.envers.boot.internal.EnversService;
import org.hibernate.envers.event.spi.EnversPostUpdateEventListenerImpl;
import org.hibernate.event.spi.PostUpdateEvent;

public class FlyEnversPostUpdateEventListenerImpl extends
        EnversPostUpdateEventListenerImpl {

    private static final long serialVersionUID = 1L;

    public FlyEnversPostUpdateEventListenerImpl(EnversService enversService) {
        super(enversService);
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        if(event.getEntity() instanceof FlyEntity) {
            boolean doNotAudit = ((FlyEntity) event.getEntity()).isDoNotAudit();

            if (!doNotAudit) {
                super.onPostUpdate(event);
            }
        } else {
            super.onPostUpdate(event);
        }
    }
}
