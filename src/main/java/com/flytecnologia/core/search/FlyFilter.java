package com.flytecnologia.core.search;

public interface FlyFilter {
    String getAcFieldDescription();

    void setAcFieldDescription(String acFieldDescription);

    String getAcFieldValue();

    void setAcFieldValue(String acFieldValue);

    String getAcExtraFieldsAutocomplete();

    void setAcExtraFieldsAutocomplete(String acExtraFieldsAutocomplete);

    String getAcValue();

    void setAcValue(String acValue);

    Integer getAcLimit();

    void setAcLimit(Integer acLimit);

    Long getId();

    void setId(Long id);

    boolean isAutoComplete();

    void setAutoComplete(boolean isAutoComplete);
}
