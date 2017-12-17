package com.flytecnologia.core.hibernate.envers;

import com.flytecnologia.core.model.FlyEntity;

import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

public class FlyEntityListener {

    @PrePersist
    public void prePersist(FlyEntity entity) {
        //entity.setOperation(FlySqlOperation.INSERT);
        // entity.setUsuarioLogado(getUser());
    }

    @PreUpdate
    public void preUpdate(FlyEntity entity) {
        //entity.setOperation(FlySqlOperation.UPDATE);
        //entity.setUsuarioLogado(getUser());
    }

    @PreRemove
    public void preRemove(FlyEntity entity) {
        //entity.setOperation(FlySqlOperation.DELETE);
        //  entity.setUsuarioLogado(getUser());
    }
}
