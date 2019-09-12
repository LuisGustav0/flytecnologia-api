package com.flytecnologia.core.hibernate.envers;

import com.flytecnologia.core.hibernate.multitenancy.FlyTenantThreadLocal;
import com.flytecnologia.core.token.FlyTokenUserDetails;
import org.hibernate.envers.RevisionListener;
import org.springframework.stereotype.Component;

@Component
public class FlyEntityRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        final FlyRevisionsEntity revEntity = (FlyRevisionsEntity) revisionEntity;

        revEntity.setUser(getUser());
        revEntity.fixTimezone();
    }

    private Long getUser() {
        final Long userId = FlyTenantThreadLocal.getUserId();

        if (userId != null)
            return userId;

        return FlyTokenUserDetails.getCurrentUserId();
    }
}
