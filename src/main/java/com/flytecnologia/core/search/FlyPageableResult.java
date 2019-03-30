package com.flytecnologia.core.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class FlyPageableResult {
    private List<?> result;
    private Integer pageNumber;
    private Integer pageSize;
    private Long totalElements;
    private Integer numberOfElements;

}