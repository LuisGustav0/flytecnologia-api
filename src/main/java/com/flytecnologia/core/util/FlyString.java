package com.flytecnologia.core.util;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Component
public class FlyString {
    public static String decapitalizeFirstLetter(String string) {
        return string == null || string.isEmpty() ? "" : Character.toLowerCase(string.charAt(0)) + string.substring(1);
    }

    public static String formatDatePtBr(LocalDate date) {
        return FlyString.formatDate(date, "dd/MM/yyyy");
    }

    public static String formatDateIntervalPtBr(LocalDate date1, LocalDate date2) {
        return FlyString.formatDatePtBr(date1) + " - " + FlyString.formatDatePtBr(date2);
    }

    public static String formatDate(LocalDate date, String pattern) {
        if(date == null)
            return null;

        DateTimeFormatter formatters = DateTimeFormatter.ofPattern(pattern);
        return date.format(formatters);
    }

    public static String formatTime(LocalTime time, String pattern) {
        if(time == null)
            return null;

        DateTimeFormatter formatters = DateTimeFormatter.ofPattern(pattern);
        return time.format(formatters);
    }

    public static String formatDecimal(BigDecimal value) {
        return formatDecimal(value, 2);
    }

    public static String formatDecimal(BigDecimal value, int digits) {
        if(value == null)
            value = BigDecimal.ZERO;

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(digits);

        return nf.format(value);
    }

    public static void main(String args[] ) {
        System.out.println(formatDecimal(new BigDecimal("12121212")));
        System.out.println(formatDecimal(new BigDecimal("5454123.12")));
        System.out.println(formatDecimal(new BigDecimal("12774843.148482"),4));
    }
}
