package com.flytecnologia.core.base;

import com.flytecnologia.core.search.FlyFilter;

public interface FlyReport<F extends FlyFilter> {
    byte[] getReport(F filter);
}
