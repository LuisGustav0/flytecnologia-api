package com.flytecnologia.core.util;

import org.springframework.stereotype.Component;

@Component
public class FlyString {
    public static String decapitalizeFirstLetter(String string) {
        return string == null || string.isEmpty() ? "" : Character.toLowerCase(string.charAt(0)) + string.substring(1);
    }
}
