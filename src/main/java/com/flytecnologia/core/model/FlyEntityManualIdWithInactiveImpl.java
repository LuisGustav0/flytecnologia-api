package com.flytecnologia.core.model;


import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
public abstract class FlyEntityManualIdWithInactiveImpl extends FlyEntityManualIdImpl implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "inactive", nullable = false)
    private boolean inactive;

    public boolean getInactive() {
        return inactive;
    }

    public void setInactive(boolean inactive) {
        this.inactive = inactive;
    }
}
