package com.flytecnologia.core.base.service.plus;

import com.flytecnologia.core.model.FlyEntity;
import org.hibernate.collection.internal.PersistentBag;
import org.springframework.util.StringUtils;

import java.util.Collection;

public class FlyValidateEmptyService {
    private FlyValidateEmptyService() {}

    public static boolean isEmpty(Object value) {
        if (value == null)
            return true;

        if (value instanceof PersistentBag) {
            return ((PersistentBag) value).isEmpty();
        }

        if (value instanceof Collection) {
            return ((Collection) value).isEmpty();
        }

        if (value instanceof Number) {
            return ((Number) value).longValue() == 0;
        }

        if (value.getClass().isArray()) {
            return ((Object[]) value).length == 0;
        }

        if (value instanceof FlyEntity) {
            return ((FlyEntity) value).getId() == null;
        }

        return StringUtils.isEmpty(value) || "undefined".equals(value) || "null".equals(value);
    }

    public static boolean isNotEmpty(Object value) {
        return !isEmpty(value);
    }

    public static void notEmpty(Object object, String message) {
        if (isEmpty(object)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }
}
