package com.flytecnologia.core.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@MappedSuperclass
public abstract class FlyEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Transient
    private Map<String, Object> parameters;

    @Transient
    @JsonIgnore
    private boolean doNotAudit;

    @Transient
    @JsonIgnore
    private boolean isIgnoreBeforeSave;

    @Transient
    @JsonIgnore
    private boolean isIgnoreAfterSave;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isDoNotAudit() {
        return doNotAudit;
    }

    public void setDoNotAudit(boolean doNotAudit) {
        this.doNotAudit = doNotAudit;
    }

    public boolean isIgnoreBeforeSave() {
        return isIgnoreBeforeSave;
    }

    public void setIgnoreBeforeSave(boolean ignoreBeforeSave) {
        isIgnoreBeforeSave = ignoreBeforeSave;
    }

    public boolean isIgnoreAfterSave() {
        return isIgnoreAfterSave;
    }

    public void setIgnoreAfterSave(boolean ignoreAfterSave) {
        isIgnoreAfterSave = ignoreAfterSave;
    }

    /*@Version
    @Column(name = "version")
    private Integer version;

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getVersion() {
        return version;
    }
*/

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

        FlyEntity other = (FlyEntity) obj;

        if (getId() == null) {
            return other.getId() == null;
        }

        return getId().equals(other.getId());
    }

    public Map<String, Object> getParameters() {
        if(parameters == null)
            parameters = new HashMap<>();

        return  parameters;
    }


    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}
