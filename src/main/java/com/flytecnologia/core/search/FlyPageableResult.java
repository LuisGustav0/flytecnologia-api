package com.flytecnologia.core.search;

import java.util.List;

public class FlyPageableResult {
    private List<?> result;
    private Integer pageNumber;
    private Integer pageSize;
    private Long totalElements;
    private Integer numberOfElements;

    public FlyPageableResult(List<?> result, Integer pageNumber,
                             Integer pageSize, Long totalElements,
                             Integer numberOfElements) {
        this.result = result;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.numberOfElements = numberOfElements;
    }

    public List<?> getResult() {
        return result;
    }

    public void setResult(List<?> result) {
        this.result = result;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Long totalElements) {
        this.totalElements = totalElements;
    }

    public Integer getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(Integer numberOfElements) {
        this.numberOfElements = numberOfElements;
    }
}