package com.flytecnologia.core.hibernate.envers;

import com.flytecnologia.core.hibernate.multitenancy.FlyTenantThreadLocal;
import com.flytecnologia.core.ip.FlyIp;
import com.flytecnologia.core.spring.FlySpringUtils;
import com.flytecnologia.core.token.FlyTokenUserDetails;
import org.hibernate.envers.RevisionListener;
import org.springframework.stereotype.Component;

@Component
public class FlyEntityRevisionListener implements RevisionListener {

    public FlyEntityRevisionListener() {
    }

    @Override
    public void newRevision(Object revisionEntity) {
        FlyIp flyIp = (FlyIp) FlySpringUtils.getBean("flyIp");

        FlyRevisionsEntity revEntity = (FlyRevisionsEntity) revisionEntity;
        revEntity.setUser(getUser());
        //revEntity.setIp(flyIp != null ? flyIp.getClientIp() : "");
        revEntity.fixTimezone();
    }

    private Long getUser() {
        Long userId = FlyTenantThreadLocal.getUserId();

        if (userId != null)
            return userId;

        //Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //return ((FlyUserDetails) auth.getPrincipal()).getUser().getId();
        return FlyTokenUserDetails.getCurrentUserId();
    }

}
