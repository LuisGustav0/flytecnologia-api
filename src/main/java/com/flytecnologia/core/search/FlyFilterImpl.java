package com.flytecnologia.core.search;

public abstract class FlyFilterImpl implements FlyFilter {
    private String acFieldDescription;
    private String acFieldValue;
    private String acExtraFieldsAutocomplete;
    private String acValue;
    private Integer acLimit;
    private Boolean acFilterDisabledRecords;
    private boolean isAutoComplete;
    private String sortGridByField;
    private String typeSortGridByField;
    private Long id;
    private Long masterDetailId;
    private String entityDetailProperty;
    private Boolean inactive;
    private boolean ignoreInactiveFilter;

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
        if (acLimit == null) {
            acLimit = 7;
        }

        return acLimit;
    }

    public void setAcLimit(Integer acLimit) {
        this.acLimit = acLimit;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean isAutoComplete() {
        return isAutoComplete;
    }

    public void setAutoComplete(boolean autoComplete) {
        this.isAutoComplete = autoComplete;
    }

    public String getSortGridByField() {
        return sortGridByField != null && !sortGridByField.contains(" ") ? sortGridByField : null;
    }

    public void setSortGridByField(String sortGridByField) {
        this.sortGridByField = sortGridByField;
    }

    public String getTypeSortGridByField() {
        return "a".equals(typeSortGridByField) ? "asc" : "desc";
    }

    public void setTypeSortGridByField(String typeSortGridByField) {
        this.typeSortGridByField = typeSortGridByField;
    }

    @Override
    public Boolean getInactive() {
        return inactive;
    }

    @Override
    public void setInactive(Boolean inactive) {
        this.inactive = inactive;
    }

    @Override
    public Boolean getAcFilterDisabledRecords() {
        return acFilterDisabledRecords;
    }

    @Override
    public void setAcFilterDisabledRecords(Boolean acFilterDisabledRecords) {
        this.acFilterDisabledRecords = acFilterDisabledRecords;
    }

    @Override
    public Long getMasterDetailId() {
        return masterDetailId;
    }

    @Override
    public void setMasterDetailId(Long masterDetailId) {
        this.masterDetailId = masterDetailId;
    }

    @Override
    public String getEntityDetailProperty() {
        return entityDetailProperty;
    }

    @Override
    public void setEntityDetailProperty(String entityDetailProperty) {
        this.entityDetailProperty = entityDetailProperty;
    }

    @Override
    public boolean isIgnoreInactiveFilter() {
        return ignoreInactiveFilter;
    }

    @Override
    public void setIgnoreInactiveFilter(boolean ignoreInactiveFilter) {
        this.ignoreInactiveFilter = ignoreInactiveFilter;
    }
}
