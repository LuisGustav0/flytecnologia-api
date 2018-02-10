package com.flytecnologia.core.search;

public class FlyAutoCompleteFilter {
    private String fieldDescription;
    private String fieldValue;
    private String extraFieldsAutocomplete;
    private String value;
    private Integer limit;

    public String getFieldDescription() {
        return fieldDescription;
    }

    public void setFieldDescription(String fieldDescription) {
        this.fieldDescription = fieldDescription;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public String getExtraFieldsAutocomplete() {
        return extraFieldsAutocomplete;
    }

    public void setExtraFieldsAutocomplete(String extraFieldsAutocomplete) {
        this.extraFieldsAutocomplete = extraFieldsAutocomplete;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getLimit() {
        return limit != null ? limit : 10;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
