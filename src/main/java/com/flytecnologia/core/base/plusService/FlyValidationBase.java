package com.flytecnologia.core.base.plusService;

import com.flytecnologia.core.exception.BusinessException;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Collection;

public interface FlyValidationBase {
    default boolean isEmpty(Object value) {
        if (value == null)
            return true;

        if (value instanceof Collection) {
            return ((Collection) value).isEmpty();
        }

        if (value instanceof Number) {
            return ((Number) value).longValue() == 0;
        }

        return StringUtils.isEmpty(value) || "undefined".equals(value) || "null".equals(value);
    }

    default boolean isNotEmpty(Object value) {
        return !isEmpty(value);
    }

    default boolean isTrue(Boolean value) {
        return value != null && value;
    }

    default boolean isFalse(Boolean value) {
        return value != null && !isTrue(value);
    }

    default boolean isTrue(Object value) {
        return isTrue((Boolean) value);
    }

    default boolean isFalse(Object value) {
        return isFalse((Boolean) value);
    }

    default void notNull(Object object, String message) {
        if (object == null) {
            throw new BusinessException(message);
        }
    }

    default void notEmpty(Object object, String message) {
        if (isEmpty(object)) {
            throw new BusinessException(message);
        }
    }

    default void validateDateLessOrEquals(LocalDate firstDate, LocalDate lastDate, String message) {
        if (firstDate == null || lastDate == null)
            return;

        if (!lastDate.isEqual(firstDate)) {
            if (lastDate.isBefore(firstDate))
                throw new BusinessException(message);
        }
    }

    default void validateDateLess(LocalDate firstDate, LocalDate lastDate, String message) {
        if (firstDate == null || lastDate == null)
            return;

        if (lastDate.isBefore(firstDate))
            throw new BusinessException(message);
    }

}
