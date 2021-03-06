package com.flytecnologia.core.search;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class FlyFilterImpl implements FlyFilter {
    private String acFieldDescription;
    private String acFieldValue;
    private String acExtraFieldsAutocomplete;
    private String acFieldsListAutocomplete;
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
    private String pdfName;
    private boolean isPreviousOrNextId;
    private boolean showAllRecordsOnSearch;

    @JsonIgnore
    private String tenantSearch;

    public Integer getAcLimit() {
        if (acLimit == null) {
            acLimit = 7;
        }

        return acLimit;
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

    public String getTypeSortGridByField() {
        return "a".equals(typeSortGridByField) ? "asc" : "desc";
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

    @Override
    public String getPdfName() {
        return pdfName;
    }

    @Override
    public void setPdfName(String pdfName) {
        this.pdfName = pdfName;
    }

    @Override
    public boolean isPreviousOrNextId() {
        return this.isPreviousOrNextId;
    }

    @Override
    public void setIsPreviousOrNextId(boolean value) {
        this.isPreviousOrNextId = value;
    }

    @Override
    public boolean isShowAllRecordsOnSearch() {
        return this.showAllRecordsOnSearch;
    }

    @Override
    public void setShowAllRecordsOnSearch(boolean value) {
        this.showAllRecordsOnSearch = value;
    }

}
