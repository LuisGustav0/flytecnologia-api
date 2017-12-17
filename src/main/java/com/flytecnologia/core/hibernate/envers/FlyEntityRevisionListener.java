package com.flytecnologia.core.hibernate.envers;

import com.flytecnologia.core.ip.FlyIp;
import com.flytecnologia.core.user.FlyUserDetails;
import com.flytecnologia.core.util.UtilSpring;
import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class FlyEntityRevisionListener implements RevisionListener {

    public FlyEntityRevisionListener(){ }

    @Override
    public void newRevision(Object revisionEntity) {
        FlyIp flyIp = (FlyIp) UtilSpring.getBean( "flyIp" );

        FlyRevisionsEntity revEntity = (FlyRevisionsEntity) revisionEntity;
        revEntity.setUser(getUser());
        revEntity.setIp(flyIp != null ? flyIp.getClientIp() : "");
        revEntity.fixTimezone();
    }

    private Long getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ((FlyUserDetails) auth.getPrincipal()).getUser().getId();
    }

}
