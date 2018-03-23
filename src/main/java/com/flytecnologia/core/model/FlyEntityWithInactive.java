package com.flytecnologia.core.model;


import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@MappedSuperclass
public abstract class FlyEntityWithInactive extends FlyEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    @Column(name = "inactive", nullable = false)
    private Boolean inactive;

    public Boolean getInactive() {
        return inactive;
    }

    public void setInactive(Boolean inactive) {
        this.inactive = inactive;
    }
}
