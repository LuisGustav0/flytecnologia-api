package com.flytecnologia.core.model;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@MappedSuperclass
public abstract class FlyEntityImpl extends FlyEntityBase implements Serializable, FlyEntity {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;
}
