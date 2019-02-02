package com.flytecnologia.core.model;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;


@Getter
@Setter
@MappedSuperclass
public abstract class FlyEntityWithInactiveImpl extends FlyEntityImpl implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "inactive", nullable = false)
    private boolean inactive;

    public boolean getInactive() {
        return inactive;
    }
}
