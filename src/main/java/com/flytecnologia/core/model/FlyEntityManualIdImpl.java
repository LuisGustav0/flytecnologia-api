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


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null || getClass() != obj.getClass())
            return false;

        FlyEntityManualIdImpl other = (FlyEntityManualIdImpl) obj;

        if (getId() == null) {
            return other.getId() == null;
        }

        return getId().equals(other.getId());
    }

}
