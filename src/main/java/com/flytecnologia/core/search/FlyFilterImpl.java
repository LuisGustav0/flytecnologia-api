package com.flytecnologia.core.search;

public abstract class FlyFilterImpl implements FlyFilter{
    private String acFieldDescription;
    private String acFieldValue;
    private String acExtraFieldsAutocomplete;
    private String acValue;
    private Integer acLimit;

    private Long id;

    public String getAcFieldDescription() {
        return acFieldDescription;
    }

    public void setAcFieldDescription(String acFieldDescription) {
        this.acFieldDescription = acFieldDescription;
    }

    public String getAcFieldValue() {
        return acFieldValue;
    }

    public void setAcFieldValue(String acFieldValue) {
        this.acFieldValue = acFieldValue;
    }

    public String getAcExtraFieldsAutocomplete() {
        return acExtraFieldsAutocomplete;
    }

    public void setAcExtraFieldsAutocomplete(String acExtraFieldsAutocomplete) {
        this.acExtraFieldsAutocomplete = acExtraFieldsAutocomplete;
    }

    public String getAcValue() {
        return acValue;
    }

    public void setAcValue(String acValue) {
        this.acValue = acValue;
    }

    public Integer getAcLimit() {
        return acLimit;
    }

    public void setAcLimit(Integer acLimit) {
        this.acLimit = acLimit;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
