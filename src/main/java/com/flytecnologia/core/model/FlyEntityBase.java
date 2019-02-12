package com.flytecnologia.core.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@MappedSuperclass
public abstract class FlyEntityBase implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Transient
    private Map<String, Object> parameters;

    @Transient
    @JsonIgnore
    private boolean ignoreAudit;

    @Transient
    @JsonIgnore
    private boolean ignoreBeforeSave;

    @Transient
    @JsonIgnore
    private boolean ignoreAfterSave;

    @Transient
    @JsonIgnore
    private String destinationTenant;

    public Map<String, Object> getParameters() {
        if (parameters == null)
            parameters = new HashMap<>();

        return parameters;
    }

    @JsonIgnore
    public boolean isIgnoreBeforeSave() {
        return ignoreBeforeSave;
    }

    @JsonIgnore
    public boolean isIgnoreAfterSave() {
        return ignoreAfterSave;
    }

    @JsonIgnore
    public boolean isIgnoreAudit() {
        return ignoreAudit;
    }


    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}
