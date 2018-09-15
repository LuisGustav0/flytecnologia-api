package com.flytecnologia.core.model;


import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
public abstract class FlyEntityManualIdImpl extends FlyEntityBase implements Serializable, FlyEntity {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(unique = true, nullable = false)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
