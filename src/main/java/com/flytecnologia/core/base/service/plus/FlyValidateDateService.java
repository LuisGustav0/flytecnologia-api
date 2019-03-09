package com.flytecnologia.core.base.service.plus;

import com.flytecnologia.core.exception.BusinessException;

import java.time.LocalDate;

public class FlyValidateDateService {
    public static void validateDateLessOrEquals(LocalDate firstDate, LocalDate lastDate, String message) {
        if (firstDate == null || lastDate == null)
            return;

        if (!lastDate.isEqual(firstDate)) {
            if (!firstDate.isBefore(lastDate))
                throw new BusinessException(message);
        }
    }

    public static void validateDateLess(LocalDate firstDate, LocalDate lastDate, String message) {
        if (firstDate == null || lastDate == null)
            return;

        if (!firstDate.isBefore(lastDate))
            throw new BusinessException(message);
    }

}
