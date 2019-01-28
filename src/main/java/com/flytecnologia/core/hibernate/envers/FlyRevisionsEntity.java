package com.flytecnologia.core.hibernate.envers;

import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Entity
@RevisionEntity(FlyEntityRevisionListener.class)
@Table(name = "audit_revision_info")
public class FlyRevisionsEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @RevisionNumber
    private int id;

    @Column(name = "id_user")
    private Long user;

    @Column
    private String ip;

    @RevisionTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column (nullable = false)
    private Date date;

    public FlyRevisionsEntity() { }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getId() {
        return id;
    }

    private static final ZoneId ZONE_ID_UTC = ZoneId.of("UTC");

    public void fixTimezone() {
        final LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZONE_ID_UTC);
        date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    @NotNull
    public LocalDateTime getRevisionDate() {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }
}
