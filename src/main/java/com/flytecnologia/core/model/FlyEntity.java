package com.flytecnologia.core.model;

import java.util.Map;

public interface FlyEntity {
    Long getId();

    void setId(Long value);

    boolean isIgnoreAudit();

    void setIgnoreAudit(boolean doNotAudit);

    boolean isIgnoreBeforeSave();

    void setIgnoreBeforeSave(boolean ignoreBeforeSave);

    boolean isIgnoreAfterSave();

    void setIgnoreAfterSave(boolean ignoreAfterSave);

    Map<String, Object> getParameters();

    void setParameters(Map<String, Object> parameters);
}
