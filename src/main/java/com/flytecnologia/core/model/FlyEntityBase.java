package com.flytecnologia.core.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@MappedSuperclass
public abstract class FlyEntityBase implements Serializable {
    private static final long serialVersionUID = 1L;

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

    @JsonIgnore
    public boolean isDoNotAudit() {
        return doNotAudit;
    }

    public void setDoNotAudit(boolean doNotAudit) {
        this.doNotAudit = doNotAudit;
    }

    @JsonIgnore
    public boolean isIgnoreBeforeSave() {
        return isIgnoreBeforeSave;
    }

    public void setIgnoreBeforeSave(boolean ignoreBeforeSave) {
        isIgnoreBeforeSave = ignoreBeforeSave;
    }

    @JsonIgnore
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
    public Map<String, Object> getParameters() {
        if (parameters == null)
            parameters = new HashMap<>();

        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}
