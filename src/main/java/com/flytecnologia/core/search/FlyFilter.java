package com.flytecnologia.core.search;

import com.lowagie.text.pdf.PdfName;

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

    Boolean getAcFilterDisabledRecords();

    void setAcFilterDisabledRecords(Boolean acFilterDisabledRecords);

    Boolean getInactive();

    void setInactive(Boolean inactive);

    Long getId();

    void setId(Long id);

    boolean isAutoComplete();

    void setAutoComplete(boolean isAutoComplete);

    String getSortGridByField();

    void setSortGridByField(String sortGridByField);

    String getTypeSortGridByField();

    void setTypeSortGridByField(String typeSortGridByField);

    Long getMasterDetailId();

    void setMasterDetailId(Long masterDetailId);

    String getEntityDetailProperty();

    void setEntityDetailProperty(String entityDetailProperty);

    boolean isIgnoreInactiveFilter();

    void setIgnoreInactiveFilter(boolean ignoreInactiveFilter);


    String getReportName();

    void setReportName(String reportName);

    String getPdfName();

    void setPdfName(String pdfName);
}
