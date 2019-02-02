package com.flytecnologia.core.hibernate.envers;

import com.flytecnologia.core.model.FlyEntity;
import org.hibernate.envers.boot.internal.EnversService;
import org.hibernate.envers.event.spi.EnversPostDeleteEventListenerImpl;
import org.hibernate.event.spi.PostDeleteEvent;

public class FlyEnversPostDeleteEventListenerImpl extends
        EnversPostDeleteEventListenerImpl {

    private static final long serialVersionUID = 1L;

    public FlyEnversPostDeleteEventListenerImpl(EnversService enversService) {
        super(enversService);
    }

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        if (event.getEntity() instanceof FlyEntity) {
            boolean doNotAudit = ((FlyEntity) event.getEntity()).isIgnoreAudit();

            if (!doNotAudit) {
                super.onPostDelete(event);
            }
        } else {
            super.onPostDelete(event);
        }
    }
}
