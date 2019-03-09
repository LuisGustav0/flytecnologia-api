package com.flytecnologia.core.base.controller.plus;

import com.flytecnologia.core.model.FlyEntity;
import com.flytecnologia.core.search.FlyFilter;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

public interface FlyTodayController<T extends FlyEntity, F extends FlyFilter> extends
        FlyGetServiceController<T, F> {
    @GetMapping(value = "/today")
    default LocalDate getToday() {
        return LocalDate.now();
    }

}
