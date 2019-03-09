package com.flytecnologia.core.base.service.plus;

public class FlyValidateBooleanService {
    public static boolean isTrue(Boolean value) {
        return value != null && value;
    }

    public static boolean isFalse(Boolean value) {
        return value != null && !isTrue(value);
    }

    public static boolean isTrue(Object value) {
        return isTrue((Boolean) value);
    }

    public static boolean isFalse(Object value) {
        return isFalse((Boolean) value);
    }

}
