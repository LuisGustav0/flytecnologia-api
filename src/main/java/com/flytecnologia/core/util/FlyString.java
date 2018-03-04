package com.flytecnologia.core.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class FlyString {
    public static String decapitalizeFirstLetter(String string) {
        return string == null || string.isEmpty() ? "" : Character.toLowerCase(string.charAt(0)) + string.substring(1);
    }

    public static String formatDatePtBr(LocalDate date) {
        return FlyString.formatDate(date, "dd/MM/yyyy");
    }

    public static String formatDate(LocalDate date, String pattern) {
        if(date == null)
            return null;

        DateTimeFormatter formatters = DateTimeFormatter.ofPattern(pattern);
        return date.format(formatters);
    }

}
