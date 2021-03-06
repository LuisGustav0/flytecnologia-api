package com.flytecnologia.core.util;

import java.util.Arrays;
import java.util.Objects;

public class FlyArray {
    private FlyArray() {}

    public static String[] cleanNullValues(String[] value) {
        if (value == null)
            return new String[]{};

        return Arrays.stream(value)
                .filter(Objects::nonNull)
                .filter(FlyArray::isNotEmpty)
                .toArray(String[]::new);
    }

    private static boolean isNotEmpty(String string) {
        return (string != null && !string.isEmpty());
    }
}
