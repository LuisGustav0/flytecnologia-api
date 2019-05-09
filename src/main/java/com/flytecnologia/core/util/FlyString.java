package com.flytecnologia.core.util;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class FlyString {
    public static String decapitalizeFirstLetter(String string) {
        return string == null || string.isEmpty() ? "" : Character.toLowerCase(string.charAt(0)) + string.substring(1);
    }

    public static String formatDateTimePtBr() {
        return formatDatePtBr() + " - " + formatTime();
    }

    public static String formatDatePtBr() {
        return formatDatePtBr(LocalDate.now());
    }

    public static String formatDatePtBr(LocalDate date) {
        return FlyString.formatDate(date, "dd/MM/yyyy");
    }

    public static String formatDateIntervalPtBr(LocalDate date1, LocalDate date2) {
        return FlyString.formatDatePtBr(date1) + " - " + FlyString.formatDatePtBr(date2);
    }

    public static String formatDate(LocalDate date, String pattern) {
        if (date == null)
            return null;

        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String formatTime() {
        return formatTime(LocalTime.now());
    }

    public static String formatTime(LocalTime time) {
        return formatTime(time, "HH:mm");
    }

    public static String formatTime(LocalTime time, String pattern) {
        if (time == null)
            return null;

        DateTimeFormatter formatters = DateTimeFormatter.ofPattern(pattern);
        return time.format(formatters);
    }

    public static String formatDateTime(LocalDateTime dateTime, String pattern) {
        if (dateTime == null)
            return null;

        DateTimeFormatter formatters = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatters);
    }

    public static String formatDecimal(BigDecimal value) {
        return formatDecimal(value, 2);
    }

    public static String formatDecimalPtBr(BigDecimal value) {
        return NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(value);
    }

    public static String formatDecimal(BigDecimal value, int digits) {
        if (value == null)
            value = BigDecimal.ZERO;

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(digits);

        return nf.format(value);
    }

    public static String getDayOfWeekPtBR(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY:
                return "Segunda-feira";
            case TUESDAY:
                return "Terça-feira";
            case WEDNESDAY:
                return "Quarta-feira";
            case THURSDAY:
                return "Quinta-feira";
            case FRIDAY:
                return "Sexta-feira";
            case SATURDAY:
                return "Sábado";
            case SUNDAY:
                return "Domingo";
        }

        return null;
    }

    public static String normalizeToASCII(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }
}

